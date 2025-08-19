CREATE DATABASE IF NOT EXISTS CafeteriaDB;
USE CafeteriaDB;

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

-- Selections (بيشاور على orders و menu_items)
CREATE TABLE selections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT NOT NULL,
    itemId INT NOT NULL,
    qty INT NOT NULL,
    FOREIGN KEY (orderId) REFERENCES orders(id),
    FOREIGN KEY (itemId) REFERENCES menu_items(id)
);

-- Discounts (مستقلة، للـloyalty program)
CREATE TABLE discounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(100)
);

-- Sales Reports (تتملأ من reporting service)
CREATE TABLE sales_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_date DATE,
    to_date DATE,
    total_sales DECIMAL(12,2),
    currency VARCHAR(3) DEFAULT 'EGP'
);

-- Redemption Reports (تتملأ من loyalty service)
CREATE TABLE redemption_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_date DATE,
    to_date DATE,
    total_redemptions INT,
    total_points INT
);

-- Insert some sample data
INSERT INTO loyalty_accounts (points) VALUES (0), (50), (100);

INSERT INTO students (studentCode, name, loyaltyAccountId) VALUES
('ST001', 'Ahmed Mohamed', 1),
('ST002', 'Sara Ali', 2),
('ST003', 'Omar Hassan', 3);

INSERT INTO menu_items (name, description, price_amount, price_currency, category) VALUES
('Chicken Sandwich', 'Grilled chicken with vegetables', 25.00, 'EGP', 'MAIN_COURSE'),
('Pizza Slice', 'Cheese pizza slice', 20.00, 'EGP', 'MAIN_COURSE'),
('Chips', 'Crispy potato chips', 10.00, 'EGP', 'SNACK'),
('Cola', 'Cold cola drink', 8.00, 'EGP', 'DRINK'),
('Water', 'Bottled water', 5.00, 'EGP', 'DRINK');
