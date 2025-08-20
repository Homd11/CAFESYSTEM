CREATE DATABASE IF NOT EXISTS CafeteriaDB;
USE CafeteriaDB;

-- Clean up existing data first
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS loyalty_accounts;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS admins;

-- Loyalty Accounts (لازم الأول عشان students بتشاور عليه)
CREATE TABLE loyalty_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    points INT DEFAULT 0
);

-- Students
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    studentCode VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    loyaltyAccountId INT,
    FOREIGN KEY (loyaltyAccountId) REFERENCES loyalty_accounts(id)
);

-- Menu Items
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_amount DECIMAL(10,2) NOT NULL,
    price_currency VARCHAR(3) DEFAULT 'EGP',
    category ENUM('MAIN_COURSE','SNACK','DRINK') NOT NULL
);

-- Orders (بيشاور على students)
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    status ENUM('NEW','PREPARING','READY') DEFAULT 'NEW',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (studentId) REFERENCES students(id)
);

-- Order Items (بيشاور على orders و menu_items)
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT NOT NULL,
    menuItemId INT NOT NULL,
    nameSnapshot VARCHAR(100),
    unitPrice_amount DECIMAL(10,2) NOT NULL,
    unitPrice_currency VARCHAR(3) DEFAULT 'EGP',
    qty INT NOT NULL,
    FOREIGN KEY (orderId) REFERENCES orders(id),
    FOREIGN KEY (menuItemId) REFERENCES menu_items(id)
);

-- Payments (بيشاور على orders)
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EGP',
    success BOOLEAN,
    txId VARCHAR(50),
    FOREIGN KEY (orderId) REFERENCES orders(id)
);

-- Create admin table
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert ONLY the loyalty accounts we actually need (exactly 3)
INSERT INTO loyalty_accounts (points) VALUES (0), (50), (100);

-- Insert exactly 3 students with their corresponding loyalty accounts
INSERT INTO students (studentCode, name, loyaltyAccountId) VALUES
('ST001', 'Ahmed Mohamed', 1),
('ST002', 'Fatma Ali', 2),
('ST003', 'Omar Hassan', 3);

INSERT INTO menu_items (name, description, price_amount, price_currency, category) VALUES
('Burger', 'Delicious beef burger', 25.00, 'EGP', 'MAIN_COURSE'),
('Pizza', 'Margherita pizza slice', 15.00, 'EGP', 'MAIN_COURSE'),
('Sandwich', 'Club sandwich', 20.00, 'EGP', 'MAIN_COURSE'),
('Chips', 'Crispy potato chips', 8.00, 'EGP', 'SNACK'),
('Cookies', 'Chocolate chip cookies', 10.00, 'EGP', 'SNACK'),
('Cola', 'Cold cola drink', 5.00, 'EGP', 'DRINK'),
('Orange Juice', 'Fresh orange juice', 8.00, 'EGP', 'DRINK'),
('Water', 'Bottled water', 3.00, 'EGP', 'DRINK');

-- Insert default admin (username: admin, password: admin123)
INSERT INTO admins (username, password_hash) VALUES
('admin', '$2a$10$rZ7G.zQa8mYl5z9x1Y2B0uKZ8qF5vH3p2wM9nC4xR6tL8sE7dQ1vG');
