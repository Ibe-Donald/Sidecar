IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'chibuikeDB')
    BEGIN
        CREATE DATABASE chibuikeDB;
    END
GO

USE chibuikeDB;
GO