# MediMeet Backend Documentation

## High-Level Design (HLD)

### System Overview
MediMeet backend is a Spring Boot-based application that provides RESTful APIs for the medical appointment booking system. It uses MongoDB for data storage and JWT for authentication.

### Architecture
- **Framework**: Spring Boot
- **Database**: MongoDB
- **Security**: Spring Security with JWT
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven

### Key Components
1. **Authentication System**
   - JWT-based authentication
   - Role-based access control
   - Token management

2. **Appointment Management**
   - Scheduling system
   - Status tracking (SCHEDULED/CANCELED/COMPLETED)
   - Doctor availability management

3. **Doctor Management**
   - Doctor profiles
   - Available time slots
   - Specialization categories

4. **User Management**
   - User profiles
   - Role management
   - Access control

### System Design
```
[Client Apps] ←→ [Spring Security Filter] ←→ [REST Controllers]
                                            ↓
[JWT Authentication] ←→ [Services] ←→ [MongoDB Repositories]
                                            ↓
                                      [MongoDB Database]
```

## Low-Level Design (LLD)

### Directory Structure
```
src/main/java/com/medimeet/app/
├── config/
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AppointmentController.java
│   └── DoctorController.java
├── dto/
│   ├── AppointmentRequest.java
│   └── DoctorResponse.java
├── exception/
│   └── CustomExceptionHandler.java
├── filter/
│   └── JwtAuthenticationFilter.java
├── model/
│   ├── Appointment.java
│   └── Doctor.java
├── repository/
│   ├── AppointmentRepository.java
│   └── DoctorRepository.java
├── security/
│   └── JwtTokenProvider.java
└── service/
    ├── AppointmentService.java
    └── DoctorService.java
```

### Component Details

#### 1. Models
```java
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String name;
    private String specialty;
    private List<LocalDateTime> availableSlots;
}

@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private String userId;
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;

    public enum AppointmentStatus {
        SCHEDULED, CANCELED, COMPLETED
    }
}
```

#### 2. Repositories
```java
@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    List<Doctor> findBySpecialty(String specialty);
}

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByUserId(String userId);
    List<Appointment> findByDoctorId(String doctorId);
}
```

#### 3. Services
```java
@Service
public class AppointmentService {
    public Appointment createAppointment(AppointmentRequest request);
    public List<Appointment> getUserAppointments(String userId);
    public void cancelAppointment(String appointmentId);
    public boolean isTimeSlotAvailable(String doctorId, LocalDateTime time);
}

@Service
public class DoctorService {
    public List<Doctor> getAllDoctors();
    public Doctor getDoctorById(String id);
    public List<Doctor> getDoctorsBySpecialty(String specialty);
}
```

### Security Implementation

#### JWT Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

## Database Design

### MongoDB Collections

#### doctors
```javascript
{
  _id: ObjectId,
  name: String,
  specialty: String,
  availableSlots: [DateTime]
}
```

#### appointments
```javascript
{
  _id: ObjectId,
  userId: String,
  doctorId: String,
  doctorName: String,
  doctorSpecialty: String,
  appointmentTime: DateTime,
  status: String
}
```

### Indexes
```javascript
// appointments collection
db.appointments.createIndex({ "userId": 1 });
db.appointments.createIndex({ "doctorId": 1 });
db.appointments.createIndex({ "appointmentTime": 1 });

// doctors collection
db.doctors.createIndex({ "specialty": 1 });
```

## API Documentation

### Authentication
```
POST /api/auth/login
POST /api/auth/register
```

### Appointments
```
POST /api/appointments
GET /api/appointments/user/{userId}
PUT /api/appointments/{id}/cancel
```

### Doctors
```
GET /api/doctors
GET /api/doctors/{id}
GET /api/doctors/specialty/{specialty}
```

## Error Handling

### Custom Exceptions
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

The application uses a simple exception handling mechanism with a custom `ResourceNotFoundException` for handling cases where requested resources are not found in the database. This exception is thrown and handled at the service layer.

### Service Layer Error Handling
```java
@Service
public class AppointmentService {
    public Appointment getAppointment(String id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }
}
```

### Controller Layer Error Handling
The controllers handle exceptions using try-catch blocks and return appropriate HTTP status codes:
- 404 for ResourceNotFoundException
- 400 for invalid requests
- 500 for internal server errors

## Testing Strategy

### Unit Tests
```java
@SpringBootTest
public class AppointmentServiceTest {
    @Test
    public void testCreateAppointment() {
        // Test appointment creation
    }
    
    @Test
    public void testCancelAppointment() {
        // Test appointment cancellation
    }
}
```

### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {
    @Test
    public void testCreateAppointmentEndpoint() {
        // Test API endpoint
    }
}
```

## Performance Optimization

### Caching
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("doctors");
    }
}
```

### MongoDB Optimization
- Proper indexing on frequently queried fields
- Pagination for large result sets
- Efficient query patterns

## Deployment

### Environment Configuration
```properties
# application.properties
spring.data.mongodb.uri=${MONGODB_URI}
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

### Docker Configuration
```dockerfile
FROM openjdk:11-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Monitoring and Logging

### Actuator Endpoints
```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
```

### Logging Configuration
```xml
<logger name="com.medimeet" level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</logger>
```

## Security Considerations

1. **Authentication**
   - JWT token validation
   - Password hashing
   - Role-based access

2. **Data Protection**
   - Input validation
   - NoSQL injection prevention
   - XSS protection

3. **API Security**
   - Rate limiting
   - CORS configuration
   - Request validation

## Scalability Considerations

1. **Database**
   - MongoDB replication
   - Sharding strategy
   - Index optimization

2. **Application**
   - Stateless design
   - Caching strategy
   - Async processing

3. **Infrastructure**
   - Load balancing
   - Auto-scaling
   - Containerization
