# Online Diagnostic Lab Management System

A Spring Boot-based backend system for managing diagnostic lab operations, appointments, and reports.

## Features

### Admin Features

- Dashboard with statistics
- Test management (CRUD operations)
- Lab employee management
- Appointment management
- Sample and report management
- User management
- Search functionality
- Report generation
- Profile management

### Lab Employee Features

- Dashboard with assigned tasks
- View test details
- Manage assigned appointments
- Search appointments
- View reports
- Profile management

### Patient Features

- Dashboard
- View test details
- Book appointments
- View appointment history
- Download medical reports
- Profile management

## Technical Stack

- Java 17
- Spring Boot 3.2.3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6 or later
- PostgreSQL 12 or later

### Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE diagnostic_lab;
```

2. Update database configuration in `src/main/resources/application.yml` if needed.

### Building and Running

1. Clone the repository:

```bash
git clone <repository-url>
```

2. Navigate to the project directory:

```bash
cd diagnostic-lab-system
```

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## API Documentation

### Authentication Endpoints

- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and get JWT token
- POST `/api/auth/refresh` - Refresh JWT token

### Test Management Endpoints

- GET `/api/tests` - Get all tests
- GET `/api/tests/{id}` - Get test by ID
- POST `/api/tests` - Create new test (Admin only)
- PUT `/api/tests/{id}` - Update test (Admin only)
- DELETE `/api/tests/{id}` - Delete test (Admin only)

### Appointment Management Endpoints

- GET `/api/appointments` - Get all appointments (Admin only)
- GET `/api/appointments/{id}` - Get appointment by ID
- POST `/api/appointments` - Create new appointment
- PUT `/api/appointments/{id}` - Update appointment status
- GET `/api/appointments/user` - Get user's appointments

### User Management Endpoints

- GET `/api/users` - Get all users (Admin only)
- GET `/api/users/{id}` - Get user by ID (Admin only)
- PUT `/api/users/{id}` - Update user
- DELETE `/api/users/{id}` - Delete user (Admin only)

## Security

The application uses JWT (JSON Web Token) for authentication and authorization. Protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

## File Upload Configuration

Report files are stored in the `uploads/reports` directory. Maximum file size and other configurations can be modified in `application.yml`.

## Error Handling

The application includes global exception handling for common scenarios:

- Invalid requests
- Resource not found
- Unauthorized access
- Server errors

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
