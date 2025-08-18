-- University Cafeteria Database - Sample Data Initialization
-- This script safely adds sample data without deleting existing records

USE cafeteriadb;

-- Create database and tables if they don't exist
CREATE DATABASE IF NOT EXISTS cafeteriadb;
USE cafeteriadb;

-- Create tables if they don't exist
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    points INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    student_code VARCHAR(50) UNIQUE NOT NULL,
    loyalty_account_id INT,
    FOREIGN KEY (loyalty_account_id) REFERENCES loyalty_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    total_amount DECIMAL(10,2),
    total_currency VARCHAR(10),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_time DECIMAL(10,2) NOT NULL,
    currency_at_time VARCHAR(10) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu(id) ON DELETE CASCADE
);

-- Insert sample loyalty accounts (only if they don't exist)
INSERT IGNORE INTO loyalty_accounts (id, points) VALUES
(1, 0),     -- For student 1
(2, 50),    -- For student 2
(3, 150),   -- For student 3
(4, 25),    -- For student 4
(5, 200);   -- For student 5

-- Insert sample students (only if they don't exist)
INSERT IGNORE INTO students (id, name, student_code, loyalty_account_id) VALUES
(1, 'Ahmed Mohamed Ali', 'STU000001', 1),
(2, 'Sara Hassan Ibrahim', 'STU000002', 2),
(3, 'Omar Khaled Mahmoud', 'STU000003', 3),
(4, 'Fatma Youssef Ahmed', 'STU000004', 4),
(5, 'Mohamed Tarek Said', 'STU000005', 5);

-- Insert comprehensive menu items (only if they don't exist)
INSERT IGNORE INTO menu (id, name, description, price, category) VALUES
-- Main Courses
(1, 'Classic Beef Burger', 'Juicy beef patty with lettuce, tomato, and special sauce', 25.00, 'MAIN_COURSE'),
(2, 'Grilled Chicken Sandwich', 'Tender grilled chicken breast with vegetables', 22.00, 'MAIN_COURSE'),
(3, 'Margherita Pizza', 'Fresh tomato sauce, mozzarella, and basil', 35.00, 'MAIN_COURSE'),
(4, 'Club Sandwich', 'Triple-layer sandwich with chicken, bacon, and vegetables', 20.00, 'MAIN_COURSE'),
(5, 'Falafel Wrap', 'Traditional Egyptian falafel with tahini sauce', 18.00, 'MAIN_COURSE'),
(6, 'Pasta Bolognese', 'Italian pasta with rich meat sauce', 28.00, 'MAIN_COURSE'),
(7, 'Grilled Fish Fillet', 'Fresh fish with lemon and herbs', 32.00, 'MAIN_COURSE'),
(8, 'Chicken Caesar Salad', 'Fresh lettuce with grilled chicken and caesar dressing', 24.00, 'MAIN_COURSE'),

-- Drinks
(9, 'Arabic Coffee', 'Traditional hot arabic coffee', 15.00, 'DRINK'),
(10, 'Turkish Coffee', 'Strong traditional turkish coffee', 12.00, 'DRINK'),
(11, 'Fresh Orange Juice', '100% natural orange juice', 12.00, 'DRINK'),
(12, 'Mango Juice', 'Fresh mango juice', 14.00, 'DRINK'),
(13, 'Coca Cola', 'Classic Coca Cola', 8.00, 'DRINK'),
(14, 'Pepsi', 'Classic Pepsi Cola', 8.00, 'DRINK'),
(15, 'Mineral Water', 'Pure mineral water', 5.00, 'DRINK'),
(16, 'Green Tea', 'Healthy green tea', 10.00, 'DRINK'),
(17, 'Black Tea', 'Classic black tea', 8.00, 'DRINK'),
(18, 'Cappuccino', 'Italian cappuccino with steamed milk', 18.00, 'DRINK'),
(19, 'Latte', 'Smooth coffee with milk', 16.00, 'DRINK'),
(20, 'Iced Coffee', 'Refreshing iced coffee', 14.00, 'DRINK'),

-- Snacks
(21, 'Crispy Chips', 'Golden crispy potato chips', 8.00, 'SNACK'),
(22, 'Chocolate Croissant', 'Buttery croissant with chocolate filling', 12.00, 'SNACK'),
(23, 'Cheese Cake', 'Rich and creamy cheesecake slice', 15.00, 'SNACK'),
(24, 'Apple Pie', 'Traditional apple pie slice', 14.00, 'SNACK'),
(25, 'Cookies (3 pieces)', 'Chocolate chip cookies', 10.00, 'SNACK'),
(26, 'Mixed Nuts', 'Assorted roasted nuts', 16.00, 'SNACK'),
(27, 'Fruit Salad', 'Fresh seasonal fruit mix', 18.00, 'SNACK'),
(28, 'Yogurt Cup', 'Natural yogurt with honey', 9.00, 'SNACK'),
(29, 'Granola Bar', 'Healthy granola energy bar', 7.00, 'SNACK'),
(30, 'Popcorn', 'Buttered popcorn', 6.00, 'SNACK');

-- Insert sample orders (only if they don't exist)
INSERT IGNORE INTO orders (id, student_id, total_amount, total_currency, order_date, status) VALUES
(1, 1, 48.00, 'EGP', '2025-08-17 10:30:00', 'READY'),
(2, 2, 47.00, 'EGP', '2025-08-17 11:45:00', 'PREPARING'),
(3, 3, 20.00, 'EGP', '2025-08-17 12:15:00', 'NEW'),
(4, 1, 56.00, 'EGP', '2025-08-18 09:20:00', 'READY'),
(5, 4, 32.00, 'EGP', '2025-08-18 10:10:00', 'PREPARING');

-- Insert sample order items (only if they don't exist)
INSERT IGNORE INTO order_items (id, order_id, menu_item_id, quantity, price_at_time, currency_at_time) VALUES
-- Order 1 (Ahmed): Burger + Coffee + Chips = 25 + 15 + 8 = 48
(1, 1, 1, 1, 25.00, 'EGP'),  -- Classic Beef Burger
(2, 1, 9, 1, 15.00, 'EGP'),  -- Arabic Coffee
(3, 1, 21, 1, 8.00, 'EGP'),  -- Crispy Chips

-- Order 2 (Sara): Pizza + Orange Juice = 35 + 12 = 47
(4, 2, 3, 1, 35.00, 'EGP'),  -- Margherita Pizza
(5, 2, 11, 1, 12.00, 'EGP'), -- Fresh Orange Juice

-- Order 3 (Omar): Club Sandwich = 20
(6, 3, 4, 1, 20.00, 'EGP'),  -- Club Sandwich

-- Order 4 (Ahmed): Pasta + Cappuccino + Cookies = 28 + 18 + 10 = 56
(7, 4, 6, 1, 28.00, 'EGP'),  -- Pasta Bolognese
(8, 4, 18, 1, 18.00, 'EGP'), -- Cappuccino
(9, 4, 25, 1, 10.00, 'EGP'), -- Cookies

-- Order 5 (Fatma): Falafel Wrap + Mango Juice = 18 + 14 = 32
(10, 5, 5, 1, 18.00, 'EGP'), -- Falafel Wrap
(11, 5, 12, 1, 14.00, 'EGP'); -- Mango Juice

-- Display summary
SELECT 'Sample data inserted successfully!' as Status;
SELECT COUNT(*) as 'Total Students' FROM students;
SELECT COUNT(*) as 'Total Menu Items' FROM menu;
SELECT COUNT(*) as 'Total Orders' FROM orders;
SELECT COUNT(*) as 'Total Order Items' FROM order_items;

-- Show sample students with their loyalty points
SELECT
    s.name as 'Student Name',
    s.student_code as 'Student Code',
    la.points as 'Loyalty Points'
FROM students s
JOIN loyalty_accounts la ON s.loyalty_account_id = la.id
ORDER BY s.id;

-- Show menu items by category
SELECT
    category as 'Category',
    COUNT(*) as 'Item Count',
    MIN(price) as 'Min Price',
    MAX(price) as 'Max Price',
    AVG(price) as 'Avg Price'
FROM menu
GROUP BY category
ORDER BY category;
