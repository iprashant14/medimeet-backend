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

## Local Development Setup

1. **Clone the Repository**
```bash
git clone https://github.com/iprashant14/medimeet-backend.git
cd medimeet-backend
```

2. **Configure Environment Variables**

Create `application-dev.properties` in `src/main/resources/`:
```properties
# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI}

# JWT Configuration
jwt.secret=${JWT_SECRET}

# Server Configuration
server.port=8080
```

3. **Create `.env` file**
```properties
# Required Environment Variables
MONGODB_URI=mongodb://localhost:27017/medimeet
JWT_SECRET=your-secure-jwt-secret
```

4. **Start MongoDB**
```bash
# Start MongoDB locally
mongod --dbpath /path/to/data/directory
```

5. **Build and Run**
```bash
# Build the project
./gradlew build

# Run in development mode
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Available Endpoints

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
