-- Migration script to add updated_at column to users and financial_record tables
-- Run this manually if your database already exists

USE finaccessdb;

-- Add updated_at column to users table (if not exists)
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
AFTER created_at;

-- Add updated_at column to financial_record table (if not exists)
ALTER TABLE financial_record 
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
AFTER created_at;

-- Verify the changes
DESCRIBE users;
DESCRIBE financial_record;
