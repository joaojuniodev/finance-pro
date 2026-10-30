ALTER TABLE goals ADD COLUMN account_id UUID REFERENCES accounts (id);

UPDATE goals AS goal
SET account_id = wallet.account_id
FROM wallets AS wallet
WHERE wallet.goal_id = goal.id;

CREATE INDEX idx_goals_account ON goals (account_id);
