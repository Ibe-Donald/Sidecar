CREATE TABLE Admins (
                        id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() PRIMARY KEY,
                        name NVARCHAR(255) NULL,
                        emailAddress NVARCHAR(255) NOT NULL,
                        password NVARCHAR(255) NOT NULL,
                        role NVARCHAR(50) NOT NULL,

                        CONSTRAINT UQ_Admins_emailAddress UNIQUE (emailAddress)
);