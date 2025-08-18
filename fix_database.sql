-- Quick fix for missing student_code column
-- Run this script to add the missing column to your existing database

USE cafeteriadb;

-- Check if student_code column exists, if not add it
SET @col_exists = (SELECT COUNT(*)
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = 'cafeteriadb'
                     AND TABLE_NAME = 'students'
                     AND COLUMN_NAME = 'student_code');

SET @sql = IF(@col_exists = 0,
              'ALTER TABLE students ADD COLUMN student_code VARCHAR(50) UNIQUE AFTER name',
              'SELECT "Column student_code already exists" AS Info');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Show the updated table structure
DESCRIBE students;

SELECT 'Database update completed!' AS Status;
