-- Insert test table
INSERT INTO _tables (tableid, tablenum) VALUES ('T1', 1);

-- Insert test staff member (password is 'testpass' encrypted with BCrypt)
INSERT INTO staff (username, password, role) 
VALUES ('teststaff', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'STAFF');

-- Insert test menu items
INSERT INTO menu_items (mealname, price, category, mealcontent, pictureofmeal) VALUES
('Test Meal 1', 10.99, 'appetizers', 'Test Description 1', 'test1.jpg'),
('Test Meal 2', 15.99, 'maincourse', 'Test Description 2', 'test2.jpg'),
('Test Meal 3', 8.99, 'dessert', 'Test Description 3', 'test3.jpg');

-- Insert test additional products
INSERT INTO additional_products (products, price, mealid) VALUES
('Test Product 1', 2.99, 1),
('Test Product 2', 3.99, 1),
('Test Product 3', 1.99, 2); 