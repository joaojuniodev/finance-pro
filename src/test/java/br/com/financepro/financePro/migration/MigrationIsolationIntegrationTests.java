package br.com.financepro.financePro.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "CAPTO_MIGRATION_TEST", matches = "true")
class MigrationIsolationIntegrationTests {
    @Test
    void productionStartsWithoutDemoDataAndCanBeValidatedAgain() throws SQLException {
        String url = required("CAPTO_MIGRATION_PROD_URL");
        assertEmptyDatabase(url);
        Flyway flyway = flyway(url, "classpath:db/migration");
        assertTrue(flyway.migrate().migrationsExecuted > 0);
        flyway.validate();
        assertEquals(0, flyway.migrate().migrationsExecuted);
        assertEquals(0, count(url, "SELECT count(*) FROM users"));
        assertEquals(0, count(url, "SELECT count(*) FROM plans"));
        assertEquals(0, count(url, "SELECT count(*) FROM transactions"));
        assertEquals(0, count(url, "SELECT count(*) FROM payments"));
        assertEquals(0, count(url, "SELECT count(*) FROM webhook_events"));
        assertTrue(count(url, "SELECT count(*) FROM roles") > 0);
        assertTrue(count(url, "SELECT count(*) FROM banks") > 0);
        assertTrue(count(url, "SELECT count(*) FROM categories WHERE system = true") > 0);
    }

    @Test
    void developmentLoadsFixturesAndCanBeValidatedAgain() throws SQLException {
        String url = required("CAPTO_MIGRATION_DEV_URL");
        assertEmptyDatabase(url);
        Flyway flyway = flyway(url, "classpath:db/migration", "classpath:db/testdata");
        assertTrue(flyway.migrate().migrationsExecuted > 0);
        flyway.validate();
        assertEquals(0, flyway.migrate().migrationsExecuted);
        assertEquals(4, count(url, "SELECT count(*) FROM users"));
        assertEquals(4, count(url, "SELECT count(*) FROM financial_profiles"));
        assertTrue(count(url, "SELECT count(*) FROM invoice_recurrence_charges") > 0);
    }

    @Test
    void upgradePreservesExistingRecordsAndLinksCardRecurrences() throws SQLException {
        String url = required("CAPTO_MIGRATION_UPGRADE_URL");
        assertEmptyDatabase(url);
        Flyway.configure().dataSource(url, required("CAPTO_MIGRATION_USER"), password())
            .schemas("public").defaultSchema("public").locations("classpath:db/migration")
            .target("43").load().migrate();
        try (Connection connection = DriverManager.getConnection(url, required("CAPTO_MIGRATION_USER"), password());
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                WITH inserted_user AS (
                    INSERT INTO users (username, password) VALUES ('migration-existing-user', 'test') RETURNING id
                ), inserted_account AS (
                    INSERT INTO accounts (user_id, current_balance) SELECT id, 200 FROM inserted_user RETURNING id
                ), inserted_goal AS (
                    INSERT INTO goals (name) VALUES ('migration-existing-goal') RETURNING id
                ), inserted_wallet AS (
                    INSERT INTO wallets (name, type, balance, account_id, goal_id)
                    SELECT 'migration-existing-card', 'CREDIT_CARD', 0, inserted_account.id, inserted_goal.id
                    FROM inserted_account CROSS JOIN inserted_goal RETURNING id, account_id
                ), inserted_invoice AS (
                    INSERT INTO invoices (wallet_id, account_id, credit_limit, status, closing_date, expiration_date, days_until_expiration)
                    SELECT id, account_id, 1000, 'PAYING', CURRENT_DATE + 20, CURRENT_DATE + 27, 7
                    FROM inserted_wallet RETURNING id
                )
                INSERT INTO recurrences (wallet_id, account_id, description, type, status, amount, frequency_type,
                    execution_type, day_one, next_execution_date)
                SELECT inserted_wallet.id, inserted_wallet.account_id, 'migration-existing-recurrence', 'DEBIT', 'ACTIVE', 10,
                    'MONTHLY', 'AUTOMATIC', EXTRACT(DAY FROM CURRENT_DATE + 1)::integer, CURRENT_DATE + 1
                FROM inserted_wallet CROSS JOIN inserted_invoice
                """);
        }
        Flyway flyway = flyway(url, "classpath:db/migration");
        assertEquals(3, flyway.migrate().migrationsExecuted);
        flyway.validate();
        assertEquals(1, count(url, "SELECT count(*) FROM users WHERE username = 'migration-existing-user'"));
        assertEquals(1, count(url, "SELECT count(*) FROM accounts WHERE current_balance = 200"));
        assertEquals(1, count(url, "SELECT count(*) FROM goals WHERE name = 'migration-existing-goal' AND account_id IS NOT NULL"));
        assertEquals(1, count(url, "SELECT count(*) FROM recurrences WHERE description = 'migration-existing-recurrence' AND execution_type = 'MANUALLY'"));
        assertTrue(count(url, "SELECT count(*) FROM invoice_recurrence_charges WHERE description = 'migration-existing-recurrence'") > 0);
    }

    private Flyway flyway(String url, String... locations) {
        return Flyway.configure().dataSource(url, required("CAPTO_MIGRATION_USER"), password())
            .schemas("public").defaultSchema("public").locations(locations).load();
    }

    private void assertEmptyDatabase(String url) throws SQLException {
        assertEquals(0, count(url, "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public'"),
            "Migration tests must run against a dedicated empty database.");
    }

    private long count(String url, String query) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, required("CAPTO_MIGRATION_USER"), password());
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(query)) {
            results.next();
            return results.getLong(1);
        }
    }

    private String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing " + name);
        return value;
    }

    private String password() {
        return System.getenv().getOrDefault("CAPTO_MIGRATION_PASSWORD", "");
    }
}
