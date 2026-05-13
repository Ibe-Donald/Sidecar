-- Backfill existing rows
UPDATE BlacklistedMerchant
SET createdAt = GETUTCDATE()
WHERE createdAt IS NULL;