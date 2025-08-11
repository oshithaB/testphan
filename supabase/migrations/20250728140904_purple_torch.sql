-- Pahana Edu Book Shop Database Schema
-- MySQL Database Schema for WAMP Server

CREATE DATABASE IF NOT EXISTS pahana_bookshop;
USE pahana_bookshop;

-- Users table (Admin and Cashier)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    telephone VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Books inventory table
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    isbn VARCHAR(20) UNIQUE,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bills table
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT NOT NULL,
    cashier_id INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    payment_status ENUM('pending', 'paid', 'cancelled') DEFAULT 'paid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (cashier_id) REFERENCES users(id)
);

-- Bill items table
CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 0,
    discount_rate DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Insert default admin user
INSERT INTO users (username, password, role, full_name, email) VALUES
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/MwIZmBlqJ3tR5.3HO', 'admin', 'System Administrator', 'admin@pahanaedu.com');
-- Password: admin123

-- Insert default cashier user  
INSERT INTO users (username, password, role, full_name, email) VALUES
('cashier', '$2a$12$9K6sKjqBUzGLXyUcBTxzN.L8kQm2nIYqMdV7zBxJ3mNqU5wP8vR9C', 'cashier', 'Default Cashier', 'cashier@pahanaedu.com');
-- Password: cashier123

-- Insert sample customers
INSERT INTO customers (account_number, name, address, telephone, email) VALUES
('CUST001', 'John Doe', '123 Main Street, Colombo 01', '0771234567', 'john.doe@email.com'),
('CUST002', 'Jane Smith', '456 Oak Avenue, Kandy', '0772345678', 'jane.smith@email.com'),
('CUST003', 'Mike Johnson', '789 Pine Road, Galle', '0773456789', 'mike.johnson@email.com');

-- Insert sample books
INSERT INTO books (title, author, isbn, category, price, quantity, description) VALUES
('Java Programming Fundamentals', 'Robert Martin', '978-0134685991', 'Programming', 2500.00, 50, 'Complete guide to Java programming'),
('Database Systems Concepts', 'Abraham Silberschatz', '978-0073523323', 'Database', 3200.00, 30, 'Comprehensive database systems textbook'),
('Web Development with HTML & CSS', 'John Duckett', '978-1118008188', 'Web Development', 1800.00, 25, 'Modern web development techniques'),
('Data Structures and Algorithms', 'Thomas Cormen', '978-0262033848', 'Computer Science', 4500.00, 20, 'Classic algorithms and data structures'),
('Network Security Essentials', 'William Stallings', '978-0134527338', 'Security', 2800.00, 15, 'Network security fundamentals');

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_customers_account_number ON customers(account_number);
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_bills_bill_number ON bills(bill_number);
CREATE INDEX idx_bills_customer_id ON bills(customer_id);
CREATE INDEX idx_bills_created_at ON bills(created_at);