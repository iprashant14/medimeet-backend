# MediMeet Backend - Medical Appointment Booking System

Spring Boot-based backend service for the MediMeet medical appointment booking application.

## Features

- **JWT Authentication**: Secure user authentication and authorization
- **MongoDB Integration**: NoSQL database for flexible data storage
- **RESTful APIs**: Well-structured APIs for appointment management
- **Doctor Management**: APIs for managing doctor profiles and availability
- **Appointment System**: Complete appointment booking and management system

## Prerequisites

- Java 11 or higher
- MongoDB 4.4 or higher
- Maven 3.6 or higher
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
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
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
POST /api/auth/login         # Login user
POST /api/auth/register      # Register new user
```

### Doctors
```
GET    /api/doctors          # Get all doctors
GET    /api/doctors/{id}     # Get doctor by ID
POST   /api/doctors         # Add new doctor
```

### Appointments
```
POST   /api/appointments           # Create appointment
GET    /api/appointments/{userId}  # Get user appointments
PUT    /api/appointments/{id}      # Update appointment
DELETE /api/appointments/{id}      # Cancel appointment
```

## Database Schema

### Doctor Collection
```javascript
{
  _id: ObjectId,
  name: String,
  specialty: String,
  availableSlots: [DateTime]
}
```

### Appointment Collection
```javascript
{
  _id: ObjectId,
  userId: String,
  doctorId: String,
  doctorName: String,
  doctorSpecialty: String,
  appointmentTime: DateTime,
  status: String (SCHEDULED/CANCELED/COMPLETED)
}
```

## Development

### Running Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn verify
```

### API Documentation
- Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- API Docs at: `http://localhost:8080/v2/api-docs`

## Deployment

1. **Build JAR file**
   ```bash
   mvn clean package
   ```

2. **Run JAR file**
   ```bash
   java -jar target/medimeet-backend-1.0.0.jar
   ```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Troubleshooting

Common issues and their solutions:

1. **MongoDB Connection Issues**
   - Verify MongoDB is running: `mongosh`
   - Check connection string in application.properties
   - Ensure MongoDB service is started

2. **Build Issues**
   - Clean and rebuild: `mvn clean install`
   - Update Maven dependencies: `mvn dependency:resolve`

## License

This project is licensed under the MIT License - see the LICENSE file for details
