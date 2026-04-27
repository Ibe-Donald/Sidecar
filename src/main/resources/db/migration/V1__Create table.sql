-- Table for blacklisted merchant
CREATE TABLE BlacklistedMerchant (
                Id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                merchantId VARCHAR(50) UNIQUE NOT NULL,
                reason VARCHAR(255),
                createdAt DATETIME DEFAULT GETDATE()
);

-- Create an index to make the JPA lookup blazing fast
CREATE INDEX idxBlacklistedMerchantId ON BlacklistedMerchant(merchantId);

-- Table for JDBC (The High-Speed Log)
CREATE TABLE TransactionLog (
                                  Id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                  cardNumber VARCHAR(20) NOT NULL,
                                  amount DECIMAL(12, 2) NOT NULL,
                                  merchantId VARCHAR(50) NOT NULL,
                                  ipAddress VARCHAR(45) NOT NULL,
                                  status VARCHAR(20) NOT NULL,
                                  executionTimeMs INT,
                                  createdAt DATETIME DEFAULT GETDATE()
);