-- University Cafeteria Database Setup Script
-- Run this script to create the complete database schema
-- This script preserves existing data and only creates missing tables/columns

-- Create database
CREATE DATABASE IF NOT EXISTS cafeteriadb;
USE cafeteriadb;

-- Create loyalty_accounts table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    points INT DEFAULT 0 NOT NULL
);

-- Create students table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    student_code VARCHAR(50) UNIQUE NOT NULL,
    loyalty_account_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loyalty_account_id) REFERENCES loyalty_accounts(id) ON DELETE CASCADE
);

-- Create menu table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category ENUM('MAIN_COURSE', 'DRINK', 'SNACK') NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    total_amount DECIMAL(10,2),
    total_currency VARCHAR(10) DEFAULT 'EGP',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Create order_items table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_time DECIMAL(10,2) NOT NULL,
    currency_at_time VARCHAR(10) DEFAULT 'EGP',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu(id) ON DELETE CASCADE
);

-- Add missing columns to existing tables if they don't exist
-- Check if student_code column exists in students table, if not add it
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'cafeteriadb'
  AND TABLE_NAME = 'students'
  AND COLUMN_NAME = 'student_code';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE students ADD COLUMN student_code VARCHAR(50) UNIQUE NOT NULL AFTER name',
    'SELECT "Column student_code already exists" AS Info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Insert sample menu items only if menu table is empty
SET @menu_count = 0;
SELECT COUNT(*) INTO @menu_count FROM menu;

-- Only insert sample data if menu is empty
SET @insert_sample_data = IF(@menu_count = 0, 1, 0);

-- Insert sample menu items if table is empty
INSERT INTO menu (name, description, price, category)
SELECT * FROM (
    SELECT 'Classic Beef Burger' as name, 'Juicy beef patty with lettuce, tomato, and special sauce' as description, 25.00 as price, 'MAIN_COURSE' as category
    UNION ALL SELECT 'Grilled Chicken Sandwich', 'Tender grilled chicken breast with vegetables', 22.00, 'MAIN_COURSE'
    UNION ALL SELECT 'Margherita Pizza', 'Fresh tomato sauce, mozzarella, and basil', 35.00, 'MAIN_COURSE'
    UNION ALL SELECT 'Club Sandwich', 'Triple-layer sandwich with chicken, bacon, and vegetables', 20.00, 'MAIN_COURSE'
    UNION ALL SELECT 'Falafel Wrap', 'Traditional Egyptian falafel with tahini sauce', 18.00, 'MAIN_COURSE'
    UNION ALL SELECT 'Pasta Bolognese', 'Italian pasta with rich meat sauce', 28.00, 'MAIN_COURSE'
    UNION ALL SELECT 'Arabic Coffee', 'Traditional hot arabic coffee', 15.00, 'DRINK'
    UNION ALL SELECT 'Fresh Orange Juice', '100% natural orange juice', 12.00, 'DRINK'
    UNION ALL SELECT 'Mango Juice', 'Fresh mango juice', 14.00, 'DRINK'
    UNION ALL SELECT 'Coca Cola', 'Classic Coca Cola', 8.00, 'DRINK'
    UNION ALL SELECT 'Mineral Water', 'Pure mineral water', 5.00, 'DRINK'
    UNION ALL SELECT 'Cappuccino', 'Italian cappuccino with steamed milk', 18.00, 'DRINK'
    UNION ALL SELECT 'Crispy Chips', 'Golden crispy potato chips', 8.00, 'SNACK'
    UNION ALL SELECT 'Chocolate Croissant', 'Buttery croissant with chocolate filling', 12.00, 'SNACK'
    UNION ALL SELECT 'Cheese Cake', 'Rich and creamy cheesecake slice', 15.00, 'SNACK'
    UNION ALL SELECT 'Apple Pie', 'Traditional apple pie slice', 14.00, 'SNACK'
    UNION ALL SELECT 'Cookies (3 pieces)', 'Chocolate chip cookies', 10.00, 'SNACK'
    UNION ALL SELECT 'Fruit Salad', 'Fresh seasonal fruit mix', 18.00, 'SNACK'
) AS sample_data
WHERE @insert_sample_data = 1;

-- Show status
SELECT
    CASE
        WHEN @menu_count = 0 THEN 'Database setup completed! Sample menu items added.'
        ELSE CONCAT('Database setup completed! Existing data preserved. Found ', @menu_count, ' existing menu items.')
    END AS Status;

-- Show tables created
SHOW TABLES;
