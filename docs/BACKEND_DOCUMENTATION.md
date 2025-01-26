# MediMeet Backend Documentation

## Current Implementation

### Technology Stack
- **Framework**: Spring Boot 3.1.4
- **Database**: MongoDB
- **Security**: Spring Security with JWT (jjwt 0.11.5)
- **Build Tool**: Gradle
- **Java Version**: 17
- **API Documentation**: SpringDoc OpenAPI 2.0.2
- **OAuth2**: Google Sign-In Integration

### Environment Configuration
The application uses environment variables for sensitive configuration:
```properties
# Required Environment Variables
MONGODB_URI=mongodb://localhost:27017/medimeet
JWT_SECRET=<secure-random-value>
GOOGLE_CLIENT_ID=<your-google-client-id>
```

### Core Services
1. **Authentication Service** (`AuthService`)
   - Google Sign-In integration
   - JWT token management
   - User authentication
   - Logout handling

2. **Doctor Service** (`DoctorService`)
   - Doctor listing and search
   - Doctor details retrieval

3. **Appointment Service** (`AppointmentService`)
   - Appointment booking
   - Appointment retrieval
   - Status tracking (SCHEDULED, CANCELED)

4. **User Details Service** (`CustomUserDetailsService`)
   - User authentication
   - User details management

### Exception Handling
The application implements a global exception handling strategy:

1. **Authentication Exceptions**
   - `UnauthorizedException`: Invalid credentials or token
   - `GoogleAuthException`: Google authentication failed

2. **Business Logic Exceptions**
   - `ResourceNotFoundException`: Requested resource not found
   - `AppointmentException`: Appointment-related errors

3. **Response Format**
```json
{
    "status": "ERROR",
    "message": "Detailed error message",
    "timestamp": "2025-01-26T22:31:55+05:30"
}
```

### API Endpoints

#### Authentication
```
POST /api/auth/google            # Google Sign-In
POST /api/auth/logout            # Logout user
```

#### Doctors
```
GET    /api/doctors              # List all doctors
GET    /api/doctors/{id}         # Get doctor details
```

#### Appointments
```
POST   /api/appointments        # Book appointment
GET    /api/appointments/user/{userId}  # Get user appointments
GET    /api/appointments/upcoming       # Get upcoming appointments
PUT    /api/appointments/{id}/cancel    # Cancel appointment
GET    /api/appointments/{id}           # Get appointment details
```

### Security Implementation

1. **JWT Authentication**
   - Access token with configurable validity
   - Stateless authentication
   - Token invalidation on logout

2. **Google OAuth2**
   - Client-side token generation
   - Server-side token verification
   - User profile management

### Error Types
- Authentication errors (invalid token, Google auth failure)
- Resource not found errors
- Appointment booking errors
- General validation errors

### Testing
- Unit tests for services
- API endpoint tests

## Future Implementations

### Planned Features
1. **Enhanced Security**
   - Refresh token mechanism
   - Rate limiting
   - Advanced error tracking

2. **Monitoring**
   - Performance metrics
   - Error logging
   - User activity tracking

3. **API Enhancements**
   - Pagination for listings
   - Advanced search filters
   - Real-time updates
