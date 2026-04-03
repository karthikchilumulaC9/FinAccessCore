# FinAccessCore - Financial Data Management System

A secure, role-based financial record management system built with Spring Boot 4.0, featuring JWT authentication, user-specific access control, and comprehensive financial analytics.

## 🚀 Features

- **JWT Authentication** - Secure token-based authentication with 24-hour expiration
- **Role-Based Access Control** - Three user roles: ADMIN, ANALYST, and VIEWER
- **User-Specific Data Access** - Users can only view and manage their own financial records
- **Financial Record Management** - Create, read, update, and delete financial transactions
- **Dashboard Analytics** - Real-time financial summaries, trends, and category-wise analysis
- **RESTful API** - Clean, well-documented REST endpoints
- **MySQL Database** - Persistent data storage with Hibernate JPA
- **Comprehensive Error Handling** - User-friendly error messages with proper HTTP status codes
- **Input Validation** - Bean validation on all request DTOs
- **Audit Logging** - Automatic timestamps for created and updated records

## 📋 Table of Contents

- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Authentication Flow](#authentication-flow)
- [User Roles](#user-roles)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Security](#security)
- [Contributing](#contributing)

## 🛠 Technology Stack

- **Java 17**
- **Spring Boot 4.0.5**
- **Spring Security 7.x** with JWT
- **Spring Data JPA** with Hibernate
- **MySQL 8.0**
- **Maven** for dependency management
- **Lombok** for reducing boilerplate code
- **BCrypt** for password hashing
- **JJWT** for JWT token generation and validation

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## 🔧 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/FinAccessCore.git
cd FinAccessCore
```

### 2. Set Up MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE finaccessdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Exit MySQL
exit
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finaccessdb
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 4. Build the Project

```bash
mvn clean install
```

## 🐳 Docker Deployment

### Quick Start with Docker

The easiest way to run FinAccessCore is using Docker Compose, which sets up both the application and MySQL database automatically.

#### Prerequisites
- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose v2.0+

#### Run with Docker Compose

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

The application will be available at `http://localhost:8082` and MySQL at `localhost:3307`.

#### What's Included

- **Spring Boot Application** - Runs on port 8082
- **MySQL Database** - Runs on port 3307 with auto-initialization
- **Health Checks** - Automatic service health monitoring
- **Persistent Storage** - MySQL data persists across restarts

#### Default Credentials

**Application**:
- Admin Username: `admin`
- Admin Password: `password123`

**MySQL**:
- Root Password: `rootpassword`
- User: `finaccess_user`
- Password: `finaccess_password`

⚠️ **Change these credentials in production!**

#### Docker Commands

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose stop

# Remove everything (clean slate)
docker-compose down -v

# Rebuild after code changes
docker-compose up -d --build
```

For detailed Docker deployment instructions, see [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md).

## ⚙️ Configuration

### Application Properties

Key configuration in `application.properties`:

```properties
# Server Configuration
server.port=8082

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finaccessdb
spring.datasource.username=root
spring.datasource.password=root

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update  # See DDL-Auto Options below
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your-256-bit-secret-key-change-this-in-production
jwt.expiration=86400000  # 24 hours
```

### Hibernate DDL-Auto Options

The `spring.jpa.hibernate.ddl-auto` property controls how Hibernate manages the database schema:

| Option | Description | Use Case |
|--------|-------------|----------|
| `create` | Drop and recreate tables on startup | Initial development, testing |
| `create-drop` | Create tables on startup, drop on shutdown | Integration tests |
| `update` | Update schema if needed (current setup) | Development |
| `validate` | Only validate schema, don't change it | Production (recommended) |
| `none` | Do nothing | Manual schema management |

**Current Setup**: `update` - Hibernate automatically creates/updates tables based on entity classes.

**Recommended for Production**: `validate` - Use manual SQL scripts for schema changes.

⚠️ **Important**: Change the JWT secret in production!

## 🚀 Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using JAR

```bash
mvn clean package
java -jar target/FinAccessCore-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8082`

### Default Admin Credentials

- **Username**: admin
- **Password**: password123

## 📚 API Documentation

### Base URL

```
http://localhost:8082/api
```

### Authentication Endpoints

#### 1. User Registration (Public)

Register a new user with VIEWER role.

```http
POST /api/users/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_VIEWER"],
  "active": true,
  "createdAt": "2026-04-03T10:00:00"
}
```

#### 2. Login

Authenticate and receive JWT token.

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

### User Management Endpoints (Admin Only)

#### 3. Create User with Specific Roles

```http
POST /api/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "analyst_user",
  "email": "analyst@example.com",
  "password": "SecurePass123!",
  "roles": ["ROLE_ANALYST"],
  "active": true
}
```

#### 4. Get All Users

```http
GET /api/users
Authorization: Bearer {token}
```

#### 5. Get Users with Pagination

```http
GET /api/users/paginated?page=0&size=10&sortBy=username&sortDirection=ASC
Authorization: Bearer {token}
```

#### 6. Get User by ID

```http
GET /api/users/{id}
Authorization: Bearer {token}
```

#### 7. Update User

```http
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "updated_username",
  "email": "updated@example.com",
  "roles": ["ROLE_ADMIN", "ROLE_ANALYST"],
  "active": true
}
```

#### 8. Deactivate User

```http
DELETE /api/users/{id}
Authorization: Bearer {token}
```

### Financial Record Endpoints

#### 9. Create Financial Record

```http
POST /api/records
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary"
}
```

**Record Types**: `INCOME`, `EXPENSE`

#### 10. Get All Records

```http
GET /api/records
Authorization: Bearer {token}
```

Returns only the authenticated user's records (or all records for admins).

#### 11. Get Records with Filters

```http
GET /api/records?category=Salary&type=INCOME&startDate=2026-01-01&endDate=2026-12-31
Authorization: Bearer {token}
```

#### 12. Get Records with Pagination

```http
GET /api/records/paginated?page=0&size=10
Authorization: Bearer {token}
```

#### 13. Get Record by ID

```http
GET /api/records/{id}
Authorization: Bearer {token}
```

#### 14. Update Record

```http
PUT /api/records/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 5500.00,
  "category": "Salary",
  "notes": "Updated salary"
}
```

#### 15. Delete Record (Soft Delete)

```http
DELETE /api/records/{id}
Authorization: Bearer {token}
```

### Dashboard Endpoints

#### 16. Get Dashboard Summary

```http
GET /api/dashboard/summary
Authorization: Bearer {token}
```

**Response:**
```json
{
  "totalIncome": 10000.00,
  "totalExpenses": 3500.00,
  "netBalance": 6500.00,
  "categoryWiseTotals": {
    "Salary": 10000.00,
    "Rent": 2000.00,
    "Groceries": 1500.00
  },
  "recentActivity": [...],
  "monthlyTrends": [...]
}
```

#### 17. Get Category Totals

```http
GET /api/dashboard/category-totals
Authorization: Bearer {token}
```

#### 18. Get Recent Activity

```http
GET /api/dashboard/recent-activity?limit=10
Authorization: Bearer {token}
```

#### 19. Get Monthly Trends

```http
GET /api/dashboard/monthly-trends
Authorization: Bearer {token}
```

## 🔐 Authentication Flow

### 1. Register or Login

```bash
# Register new user
curl -X POST http://localhost:8082/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'

# Login
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "SecurePass123!"
  }'
```

### 2. Use Token in Requests

```bash
# Store token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Make authenticated request
curl -X GET http://localhost:8082/api/records \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Token Expiration

Tokens expire after 24 hours. After expiration, login again to get a new token.

## 👥 User Roles

### ROLE_VIEWER
- View own financial records
- View own dashboard
- Cannot create, update, or delete records
- Cannot access user management

### ROLE_ANALYST
- All VIEWER permissions
- Create, update, and delete own financial records
- View detailed analytics

### ROLE_ADMIN
- All ANALYST permissions
- View ALL users' financial records
- Create, update, and delete users
- Assign roles to users
- Access all system features

## 🗄️ Database Schema

### Schema Management

The project uses **Hibernate JPA** for automatic table creation during development:

```properties
# Development (current setup)
spring.jpa.hibernate.ddl-auto=update
```

This automatically creates and updates tables based on JPA entity annotations.

### For Production

Change to manual schema management:

```properties
# Production (recommended)
spring.jpa.hibernate.ddl-auto=validate
```

Then use the provided SQL scripts:

1. **Initial Setup**: Run `schema.sql` to create tables
2. **Sample Data**: Run `data.sql` to insert default users
3. **Migrations**: Use migration scripts for schema changes

### Tables Created by Hibernate

#### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### User Roles Table

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Financial Records Table

```sql
CREATE TABLE financial_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    notes TEXT,
    user_id BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test

```bash
mvn test -Dtest=UserServiceTest
```

### Test Coverage

The project includes:
- Unit tests for services
- Integration tests for controllers
- Repository tests

## 🔒 Security

### Password Security
- Passwords are hashed using BCrypt with strength 10
- Passwords are never stored in plain text
- Minimum password length: 8 characters

### JWT Security
- Tokens are signed using HMAC-SHA256
- Tokens expire after 24 hours
- Tokens include user roles for authorization

### API Security
- All endpoints (except login and register) require authentication
- Role-based access control on all endpoints
- Users can only access their own data (except admins)

### Best Practices
- Change default admin password immediately
- Use strong JWT secret in production (minimum 256 bits)
- Enable HTTPS in production
- Implement rate limiting for login endpoints
- Regular security audits

## 📝 Error Handling

The API returns standardized error responses:

```json
{
  "timestamp": "2026-04-03T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is required",
  "path": "/api/users/register",
  "validationErrors": {
    "username": "Username is required"
  }
}
```

### HTTP Status Codes

- `200 OK` - Successful GET/PUT request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource
- `500 Internal Server Error` - Server error

## 🚀 Deployment

### Production Checklist

- [ ] Change JWT secret to a secure random string
- [ ] Update database credentials
- [ ] Change default admin password
- [ ] Enable HTTPS
- [ ] Set up database backups
- [ ] Configure logging
- [ ] Set up monitoring
- [ ] Enable CORS for frontend domain
- [ ] Review and update security settings

### Environment Variables

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/finaccessdb
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_SECRET=your-production-secret-key
export SERVER_PORT=8082
```

## 📖 Additional Documentation

- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Complete API reference
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Docker deployment guide
- [TECHNICAL_DECISIONS.md](TECHNICAL_DECISIONS.md) - Technical decisions and trade-offs
- [AUTHENTICATION.md](AUTHENTICATION.md) - Detailed authentication guide (if exists)
- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture overview (if exists)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Authors

- Karthik chilumula - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- JWT.io for JWT implementation
- MySQL team for the robust database

**Built with ❤️ using Spring Boot**
