DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'invoices'
          AND column_name = 'limit'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'invoices'
          AND column_name = 'credit_limit'
    ) THEN
        ALTER TABLE invoices
            RENAME COLUMN "limit" TO credit_limit;
    END IF;
END $$;
