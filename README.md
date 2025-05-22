# OrderMe Restaurant Management System

OrderMe is a comprehensive restaurant management system that enables restaurants to manage their operations efficiently. The system includes features for menu management, order processing, table management, and staff operations.

## Features

- **Authentication & Authorization**
  - Staff login system
  - Table-based client authentication
  - Role-based access control

- **Menu Management**
  - Create, read, update menu items
  - Categorize menu items
  - Price management

- **Order Management**
  - Real-time order processing
  - Order status tracking
  - Table-based ordering system

- **Table Management**
  - Dynamic table allocation
  - Table status tracking
  - QR code-based ordering

- **User Management**
  - Staff account management
  - User role management
  - Secure password handling

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Redis 7.0 or higher
- PostgreSQL 12 or higher
- Node.js 16 or higher (for frontend)
- npm 8 or higher (for frontend)

## Tech Stack

### Backend
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Redis for caching
- PostgreSQL for database
- JUnit 5 for testing
- TestContainers for integration testing

### Frontend
- React
- TypeScript
- Material-UI
- Redux Toolkit
- React Router

## Project Structure

```
orderme/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/orderme/backend/
│   │   │   │       ├── config/        # Configuration classes
│   │   │   │       ├── controller/    # REST controllers
│   │   │   │       ├── dto/           # Data Transfer Objects
│   │   │   │       ├── menu/          # Menu management
│   │   │   │       ├── order/         # Order processing
│   │   │   │       ├── security/      # Security configuration
│   │   │   │       ├── staff/         # Staff management
│   │   │   │       ├── table/         # Table management
│   │   │   │       └── user/          # User management
│   │   │   └── resources/
│   │   │       └── application.yml    # Application configuration
│   │   └── test/                      # Test classes
│   └── pom.xml                        # Maven configuration
└── frontend/               # React frontend
    ├── src/
    │   ├── components/    # React components
    │   ├── pages/        # Page components
    │   ├── services/     # API services
    │   └── store/        # Redux store
    └── package.json      # NPM configuration
```

## Setup Instructions

### Backend Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/orderme.git
   cd orderme/backend
   ```

2. Configure the database:
   - Create a PostgreSQL database named `orderme`
   - Update `application.yml` with your database credentials

3. Configure Redis:
   - Install Redis 7.0 or higher
   - Ensure Redis is running on port 6379 (default)

4. Build the project:
   ```bash
   mvn clean install
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd ../frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Application

### Backend

1. Start the backend server:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The backend server will start on `http://localhost:8080`

### Frontend

1. Start the frontend development server:
   ```bash
   cd frontend
   npm start
   ```
   The frontend application will start on `http://localhost:3000`

## Testing

### Backend Tests

Run the backend tests using Maven:
```bash
cd backend
mvn test
```

The test suite includes:
- Unit tests
- Integration tests
- API endpoint tests

### Frontend Tests

Run the frontend tests using npm:
```bash
cd frontend
npm test
```

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui.html` when the backend server is running.

### Key API Endpoints

- Authentication:
  - POST `/api/auth/login` - Staff login
  - POST `/api/auth/id-login` - Table-based client login

- Menu Management:
  - GET `/api/menu` - Get all menu items
  - POST `/api/menu` - Create menu item
  - GET `/api/menu/{id}` - Get menu item by ID

- Order Management:
  - POST `/api/orders` - Create order
  - GET `/api/orders` - Get all orders
  - PUT `/api/orders/{id}/status` - Update order status

- Table Management:
  - GET `/api/tables` - Get all tables
  - POST `/api/tables` - Create table
  - DELETE `/api/tables/{id}` - Delete table

- User Management:
  - GET `/api/users` - Get all users
  - POST `/api/users` - Create user
  - PUT `/api/users/{id}` - Update user
  - DELETE `/api/users/{id}` - Delete user

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please open an issue in the GitHub repository or contact the development team.
