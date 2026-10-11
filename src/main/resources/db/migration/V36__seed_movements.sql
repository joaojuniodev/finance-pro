-- Internal transfers distribute the same funds; they are not income/expenses.
INSERT INTO movements (
    amount,
    from_wallet,
    to_wallet,
    registered_at
)
SELECT
    seed.amount,
    source.id,
    destination.id,
    CURRENT_TIMESTAMP
FROM (
    VALUES
        (
            300.00,
            'Dinheiro físico'
        ),
        (
            1500.00,
            'Reserva de emergência'
        ),
        (
            1000.00,
            'Investimentos'
        ),
        (
            500.00,
            'Poupança de férias'
        ),
        (
            200.00,
            'Saldo digital'
        )
) AS seed (
    amount,
    destination_name
)
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS source
    ON source.account_id = account.id
   AND source.name = 'Conta principal'
JOIN wallets AS destination
    ON destination.account_id = account.id
   AND destination.name = seed.destination_name;
