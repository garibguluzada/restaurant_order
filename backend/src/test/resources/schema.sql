-- Create tables table
CREATE TABLE IF NOT EXISTS _tables (
    tableid VARCHAR(50) PRIMARY KEY,
    tablenum INTEGER NOT NULL
);

-- Create staff table
CREATE TABLE IF NOT EXISTS staff (
    staffid BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    orderid INTEGER AUTO_INCREMENT PRIMARY KEY,
    tableid VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    totalcost DECIMAL(10,2) NOT NULL,
    mealname VARCHAR(100) NOT NULL,
    createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tableid) REFERENCES _tables(tableid)
);

-- Create menu_items table
CREATE TABLE IF NOT EXISTS menu_items (
    mealid INTEGER AUTO_INCREMENT PRIMARY KEY,
    mealname VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    mealcontent TEXT,
    pictureofmeal VARCHAR(255)
);

-- Create additional_products table
CREATE TABLE IF NOT EXISTS additional_products (
    prodid INTEGER AUTO_INCREMENT PRIMARY KEY,
    products VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    mealid INTEGER,
    FOREIGN KEY (mealid) REFERENCES menu_items(mealid)
); 