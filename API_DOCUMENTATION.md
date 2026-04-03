# FinAccessCore API Documentation

Complete API reference for the FinAccessCore Financial Management System.

**Base URL**: `http://localhost:8082/api`

**Version**: 1.0.0

---

## Table of Contents

1. [Authentication](#authentication)
2. [User Management](#user-management)
3. [Financial Records](#financial-records)
4. [Dashboard & Analytics](#dashboard--analytics)
5. [Error Responses](#error-responses)
6. [Data Models](#data-models)

---

## Authentication

### Overview

The API uses JWT (JSON Web Token) for authentication. After successful login, include the token in the `Authorization` header for all protected endpoints.

**Header Format**: `Authorization: Bearer {token}`

**Token Expiration**: 24 hours

---

### 1. Register User

Create a new user account with default VIEWER role.

**Endpoint**: `POST /api/users/register`

**Access**: Public (No authentication required)

**Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Validation Rules**:
- `username`: 3-50 characters, required
- `email`: Valid email format, required
- `password`: Minimum 8 characters, required

**Success Response** (201 Created):
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

**Error Responses**:
- `400 Bad Request`: Invalid input or validation failure
- `409 Conflict`: Username or email already exists

**Example cURL**:
```bash
curl -X POST http://localhost:8082/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

---

### 2. Login

Authenticate user and receive JWT token.

**Endpoint**: `POST /api/auth/login`

**Access**: Public

**Request Body**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Success Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjQ2MzA0MDAwLCJleHAiOjE2NDYzOTA0MDB9.signature",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

**Error Responses**:
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Missing username or password

**Example cURL**:
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

---

## User Management

All user management endpoints require **ADMIN** role.

---

### 3. Create User (Admin)

Create a new user with specific roles.

**Endpoint**: `POST /api/users`

**Access**: ADMIN only

**Headers**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body**:
```json
{
  "username": "analyst_user",
  "email": "analyst@example.com",
  "password": "SecurePass123!",
  "roles": ["ROLE_ANALYST"],
  "active": true
}
```

**Available Roles**:
- `ROLE_ADMIN`: Full system access
- `ROLE_ANALYST`: Can manage own financial records
- `ROLE_VIEWER`: Read-only access to own records

**Success Response** (201 Created):
```json
{
  "id": 2,
  "username": "analyst_user",
  "email": "analyst@example.com",
  "roles": ["ROLE_ANALYST"],
  "active": true,
  "createdAt": "2026-04-03T10:00:00"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Missing or invalid token
- `403 Forbidden`: Insufficient permissions
- `409 Conflict`: Username or email already exists

---

### 4. Get All Users

Retrieve list of all users.

**Endpoint**: `GET /api/users`

**Access**: ADMIN only

**Headers**:
```
Authorization: Bearer {token}
```

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "roles": ["ROLE_ADMIN"],
    "active": true,
    "createdAt": "2026-04-01T10:00:00"
  },
  {
    "id": 2,
    "username": "analyst_user",
    "email": "analyst@example.com",
    "roles": ["ROLE_ANALYST"],
    "active": true,
    "createdAt": "2026-04-03T10:00:00"
  }
]
```

---

### 5. Get Users (Paginated)

Retrieve users with pagination and sorting.

**Endpoint**: `GET /api/users/paginated`

**Access**: ADMIN only

**Query Parameters**:
- `page` (optional): Page number, default: 0
- `size` (optional): Page size, default: 10
- `sortBy` (optional): Sort field, default: "id"
- `sortDirection` (optional): "ASC" or "DESC", default: "ASC"

**Example Request**:
```
GET /api/users/paginated?page=0&size=10&sortBy=username&sortDirection=ASC
```

**Success Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "roles": ["ROLE_ADMIN"],
      "active": true,
      "createdAt": "2026-04-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1
}
```

---

### 6. Get User by ID

Retrieve a specific user by ID.

**Endpoint**: `GET /api/users/{id}`

**Access**: ADMIN only

**Path Parameters**:
- `id`: User ID (Long)

**Success Response** (200 OK):
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"],
  "active": true,
  "createdAt": "2026-04-01T10:00:00"
}
```

**Error Responses**:
- `404 Not Found`: User not found

---

### 7. Update User

Update user information.

**Endpoint**: `PUT /api/users/{id}`

**Access**: ADMIN only

**Path Parameters**:
- `id`: User ID (Long)

**Request Body** (all fields optional):
```json
{
  "username": "updated_username",
  "email": "updated@example.com",
  "password": "NewSecurePass123!",
  "roles": ["ROLE_ADMIN", "ROLE_ANALYST"],
  "active": true
}
```

**Success Response** (200 OK):
```json
{
  "id": 1,
  "username": "updated_username",
  "email": "updated@example.com",
  "roles": ["ROLE_ADMIN", "ROLE_ANALYST"],
  "active": true,
  "createdAt": "2026-04-01T10:00:00"
}
```

**Error Responses**:
- `404 Not Found`: User not found
- `409 Conflict`: Username or email already exists

---

### 8. Deactivate User

Soft delete a user (sets active = false).

**Endpoint**: `DELETE /api/users/{id}`

**Access**: ADMIN only

**Path Parameters**:
- `id`: User ID (Long)

**Business Rules**:
- Cannot deactivate the last active admin user

**Success Response** (204 No Content)

**Error Responses**:
- `404 Not Found`: User not found
- `400 Bad Request`: Cannot deactivate last admin

---

## Financial Records

All authenticated users can access these endpoints. Users see only their own records (except admins who see all records).

---

### 9. Create Financial Record

Create a new financial transaction.

**Endpoint**: `POST /api/records`

**Access**: All authenticated users

**Request Body**:
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary payment"
}
```

**Field Descriptions**:
- `amount`: Transaction amount (Decimal, > 0, required)
- `type`: Transaction type (INCOME or EXPENSE, required)
- `category`: Category name (String, required)
- `date`: Transaction date (ISO date format, required)
- `notes`: Additional notes (String, optional)

**Success Response** (201 Created):
```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary payment",
  "userId": 1,
  "username": "admin"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid input or validation failure
- `401 Unauthorized`: Missing or invalid token

**Example cURL**:
```bash
curl -X POST http://localhost:8082/api/records \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "Monthly salary"
  }'
```

---

### 10. Get All Records

Retrieve all financial records (user-specific or all for admins).

**Endpoint**: `GET /api/records`

**Access**: All authenticated users

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "Monthly salary",
    "userId": 1,
    "username": "admin"
  },
  {
    "id": 2,
    "amount": 1200.00,
    "type": "EXPENSE",
    "category": "Rent",
    "date": "2026-04-05",
    "notes": "Monthly rent",
    "userId": 1,
    "username": "admin"
  }
]
```

---

### 11. Get Records with Filters

Filter financial records by category, type, and date range.

**Endpoint**: `GET /api/records`

**Access**: All authenticated users

**Query Parameters** (all optional):
- `category`: Filter by category (String)
- `type`: Filter by type (INCOME or EXPENSE)
- `startDate`: Start date (ISO format: YYYY-MM-DD)
- `endDate`: End date (ISO format: YYYY-MM-DD)

**Example Request**:
```
GET /api/records?category=Salary&type=INCOME&startDate=2026-01-01&endDate=2026-12-31
```

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "Monthly salary",
    "userId": 1,
    "username": "admin"
  }
]
```

---

### 12. Get Records (Paginated)

Retrieve records with pagination.

**Endpoint**: `GET /api/records/paginated`

**Access**: All authenticated users

**Query Parameters**:
- `page` (optional): Page number, default: 0
- `size` (optional): Page size, default: 10

**Example Request**:
```
GET /api/records/paginated?page=0&size=10
```

**Success Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "amount": 5000.00,
      "type": "INCOME",
      "category": "Salary",
      "date": "2026-04-01",
      "notes": "Monthly salary",
      "userId": 1,
      "username": "admin"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

---

### 13. Get Record by ID

Retrieve a specific financial record.

**Endpoint**: `GET /api/records/{id}`

**Access**: All authenticated users (own records only, admins see all)

**Path Parameters**:
- `id`: Record ID (Long)

**Success Response** (200 OK):
```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary",
  "userId": 1,
  "username": "admin"
}
```

**Error Responses**:
- `404 Not Found`: Record not found
- `403 Forbidden`: Cannot access another user's record

---

### 14. Update Financial Record

Update an existing financial record.

**Endpoint**: `PUT /api/records/{id}`

**Access**: All authenticated users (own records only, admins can update all)

**Path Parameters**:
- `id`: Record ID (Long)

**Request Body** (all fields optional):
```json
{
  "amount": 5500.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Updated salary amount"
}
```

**Success Response** (200 OK):
```json
{
  "id": 1,
  "amount": 5500.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Updated salary amount",
  "userId": 1,
  "username": "admin"
}
```

**Error Responses**:
- `404 Not Found`: Record not found
- `403 Forbidden`: Cannot update another user's record

---

### 15. Delete Financial Record

Soft delete a financial record (sets deleted = true).

**Endpoint**: `DELETE /api/records/{id}`

**Access**: All authenticated users (own records only, admins can delete all)

**Path Parameters**:
- `id`: Record ID (Long)

**Success Response** (204 No Content)

**Error Responses**:
- `404 Not Found`: Record not found
- `403 Forbidden`: Cannot delete another user's record

---

## Dashboard & Analytics

Dashboard endpoints provide financial summaries and analytics.

---

### 16. Get Dashboard Summary

Get comprehensive financial summary including totals, categories, and trends.

**Endpoint**: `GET /api/dashboard/summary`

**Access**: All authenticated users

**Success Response** (200 OK):
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
  "recentActivity": [
    {
      "id": 5,
      "amount": 5000.00,
      "type": "INCOME",
      "category": "Salary",
      "date": "2026-04-01",
      "notes": "Monthly salary",
      "userId": 1,
      "username": "admin"
    }
  ],
  "monthlyTrends": [
    {
      "period": "2026-03",
      "income": 5000.00,
      "expense": 1500.00
    },
    {
      "period": "2026-04",
      "income": 5000.00,
      "expense": 2000.00
    }
  ]
}
```

**Field Descriptions**:
- `totalIncome`: Sum of all income transactions
- `totalExpenses`: Sum of all expense transactions
- `netBalance`: Total income minus total expenses
- `categoryWiseTotals`: Breakdown by category
- `recentActivity`: Last 5 transactions
- `monthlyTrends`: Income and expenses by month

---

### 17. Get Category Totals

Get spending/income breakdown by category.

**Endpoint**: `GET /api/dashboard/category-totals`

**Access**: All authenticated users

**Success Response** (200 OK):
```json
[
  {
    "category": "Salary",
    "total": 10000.00
  },
  {
    "category": "Rent",
    "total": 2000.00
  },
  {
    "category": "Groceries",
    "total": 1500.00
  }
]
```

---

### 18. Get Recent Activity

Get most recent financial transactions.

**Endpoint**: `GET /api/dashboard/recent-activity`

**Access**: All authenticated users

**Query Parameters**:
- `limit` (optional): Number of records to return, default: 5

**Example Request**:
```
GET /api/dashboard/recent-activity?limit=10
```

**Success Response** (200 OK):
```json
[
  {
    "id": 5,
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "Monthly salary",
    "userId": 1,
    "username": "admin"
  }
]
```

---

### 19. Get Monthly Trends

Get income and expense trends by month.

**Endpoint**: `GET /api/dashboard/monthly-trends`

**Access**: All authenticated users

**Success Response** (200 OK):
```json
[
  {
    "period": "2026-01",
    "income": 5000.00,
    "expense": 1200.00
  },
  {
    "period": "2026-02",
    "income": 5000.00,
    "expense": 1500.00
  },
  {
    "period": "2026-03",
    "income": 5500.00,
    "expense": 1800.00
  }
]
```

**Field Descriptions**:
- `period`: Month in YYYY-MM format
- `income`: Total income for the month
- `expense`: Total expenses for the month

---

## Error Responses

All error responses follow a standard format:

```json
{
  "timestamp": "2026-04-03T10:00:00.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is required",
  "path": "/api/users/register",
  "validationErrors": {
    "username": "Username is required",
    "email": "Invalid email format"
  }
}
```

### HTTP Status Codes

| Code | Description | Common Causes |
|------|-------------|---------------|
| 200 | OK | Successful GET/PUT request |
| 201 | Created | Successful POST request |
| 204 | No Content | Successful DELETE request |
| 400 | Bad Request | Invalid input, validation failure |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource (username/email) |
| 500 | Internal Server Error | Server error |

### Common Error Messages

#### Authentication Errors

**401 Unauthorized**:
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "The username or password you entered is incorrect. Please try again."
}
```

**403 Forbidden**:
```json
{
  "status": 403,
  "error": "Access Denied",
  "message": "You don't have permission to access this resource. Please check your role and permissions."
}
```

#### Validation Errors

**400 Bad Request**:
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed. Please check the errors.",
  "validationErrors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters long"
  }
}
```

#### Resource Errors

**404 Not Found**:
```json
{
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found. The user may have been deleted or does not exist."
}
```

**409 Conflict**:
```json
{
  "status": 409,
  "error": "Duplicate Resource",
  "message": "User with username 'john_doe' already exists"
}
```

---

## Data Models

### User

```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"],
  "active": true,
  "createdAt": "2026-04-01T10:00:00"
}
```

**Fields**:
- `id` (Long): Unique identifier
- `username` (String): Username (3-50 chars, unique)
- `email` (String): Email address (unique)
- `roles` (Array): User roles
- `active` (Boolean): Account status
- `createdAt` (DateTime): Account creation timestamp

### Financial Record

```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary",
  "userId": 1,
  "username": "admin"
}
```

**Fields**:
- `id` (Long): Unique identifier
- `amount` (Decimal): Transaction amount
- `type` (Enum): INCOME or EXPENSE
- `category` (String): Category name
- `date` (Date): Transaction date
- `notes` (String): Optional notes
- `userId` (Long): Owner user ID
- `username` (String): Owner username

### Login Response

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

**Fields**:
- `token` (String): JWT access token
- `type` (String): Token type (always "Bearer")
- `id` (Long): User ID
- `username` (String): Username
- `email` (String): Email address
- `roles` (Array): User roles

---

## Rate Limiting

Currently, there is no rate limiting implemented. For production, consider implementing:

- Login endpoint: 5 requests per minute per IP
- Registration endpoint: 3 requests per hour per IP
- Other endpoints: 100 requests per minute per user

---

## Versioning

Current API version: **v1.0.0**

The API version is not included in the URL. Future versions will be backward compatible or will use URL versioning (`/api/v2/...`).

---

## Support

For API support or questions:
- Email: support@finaccesscore.com
- GitHub Issues: [Create an issue](https://github.com/yourusername/FinAccessCore/issues)

---

**Last Updated**: April 3, 2026
