package br.com.financepro.financePro.transaction.repository.spec;

import br.com.financepro.financePro.transaction.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

public class TransactionSpecification {

    Specification<Transaction> spec;

    public TransactionSpecification() {
        this.spec = Specification.where(null);
    }

    public Specification<Transaction> apply() {
        return this.spec;
    }

    public void addToSpecifications(UUID accountId, Integer month, Integer year) {
        if (accountId != null) hasAccount(accountId);

        if (year != null && month != null) {
            betweenMonthYear(month, year);
        } else if (year != null) {
            betweenYear(year);
        }
    }

    private void hasAccount(UUID accountId) {
        this.spec = this.spec.and((root, query, cb ) ->
            cb.equal(root.get("account").get("id"), accountId));
    }

    private void betweenMonthYear(Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        this.spec = this.spec.and((root, query, cb) ->
            cb.between(root.get("registeredAt"), start, end));
    }

    private void betweenYear(Integer year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        this.spec = this.spec.and((root, query, cb) ->
            cb.between(root.get("registeredAt"), start, end));
    }
}