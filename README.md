# MediMeet Backend - Medical Appointment Booking System

Spring Boot-based backend service for the MediMeet medical appointment booking application.

## Technology Stack

- **Framework**: Spring Boot 3.1.4
- **Database**: MongoDB
- **Security**: Spring Security with JWT (jjwt 0.11.5)
- **Build Tool**: Gradle
- **Java Version**: 17
- **API Documentation**: SpringDoc OpenAPI 2.0.2

## Features

- **JWT Authentication**: Secure user authentication and authorization
- **MongoDB Integration**: NoSQL database for flexible data storage
- **RESTful APIs**: Well-structured APIs for appointment management
- **Doctor Management**: APIs for managing doctor profiles and availability
- **Appointment System**: Complete appointment booking and management system
- **OpenAPI Documentation**: Interactive API documentation with Swagger UI

## Prerequisites

- Java 17 or higher
- MongoDB 4.4 or higher
- Gradle 7.x+ or compatible version
- Git

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/iprashant14/medimeet-backend.git
   cd medimeet-backend
   ```

2. **Configure MongoDB**
   - Install MongoDB if not already installed
   - Create a database named 'medimeet'
   - Update application.properties with your MongoDB URI

3. **Configure Application Properties**
   Create `src/main/resources/application.properties`:
   ```properties
   # MongoDB Configuration
   spring.data.mongodb.uri=mongodb://localhost:27017/medimeet
   
   # JWT Configuration
   jwt.secret=your-secret-key
   jwt.expiration=86400000
   
   # Server Configuration
   server.port=8080
   
   # Swagger UI path
   springdoc.swagger-ui.path=/swagger-ui.html
   ```

4. **Build and Run**
   ```bash
   # Build the project
   ./gradlew build
   
   # Run the application
   ./gradlew bootRun
   ```

## Project Structure

```
src/main/java/com/medimeet/app/
├── config/         # Configuration classes
├── controller/     # REST controllers
├── dto/           # Data Transfer Objects
├── exception/     # Custom exceptions
├── filter/        # JWT and security filters
├── model/         # MongoDB entities
├── repository/    # MongoDB repositories
├── security/      # Security configurations
└── service/       # Business logic
```

## API Endpoints

### Authentication
```
POST /api/auth/register    # User registration
POST /api/auth/login       # User login
POST /api/auth/refresh     # Refresh token
```

### Doctors
```
GET    /api/doctors        # List all doctors
GET    /api/doctors/{id}   # Get doctor details
POST   /api/doctors        # Add new doctor
```

### Appointments
```
POST   /api/appointments           # Book appointment
GET    /api/appointments/{userId}  # Get user appointments
PUT    /api/appointments/{id}      # Update appointment
DELETE /api/appointments/{id}      # Cancel appointment
```

## Error Handling

The application currently implements basic error handling:
- Resource not found errors via `ResourceNotFoundException`

TODO: Implement comprehensive error handling:
- [ ] Add global exception handler using `@ControllerAdvice`
- [ ] Add validation error handling
- [ ] Add authentication error handling
- [ ] Add database operation error handling
- [ ] Add generic server error handling

## Testing Status

The application currently has minimal test coverage:
- Basic context loading test (`AppApplicationTests`)

TODO: Implement comprehensive testing:
- [ ] Unit tests for services and repositories
- [ ] Integration tests for API endpoints
- [ ] Security tests for authentication
- [ ] Performance and load testing

## Future Enhancements

### Caching Implementation
- [ ] Redis integration for distributed caching
- [ ] Cache for frequently accessed data
- [ ] Cache invalidation strategy

### Kubernetes Deployment
- [ ] Helm charts for deployment
- [ ] Container configuration
- [ ] Resource management
- [ ] Auto-scaling setup

### API Gateway
- [ ] Request routing
- [ ] Rate limiting
- [ ] Load balancing
- [ ] Security policies

### Monitoring
- [ ] Prometheus metrics
- [ ] Grafana dashboards
- [ ] ELK stack for logging
- [ ] Health checks

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
