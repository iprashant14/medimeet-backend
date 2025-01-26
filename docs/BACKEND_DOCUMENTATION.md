# MediMeet Backend Documentation

## Current Implementation

### Technology Stack
- **Framework**: Spring Boot 3.1.4
- **Database**: MongoDB
- **Security**: Spring Security with JWT (jjwt 0.11.5)
- **Build Tool**: Gradle
- **Java Version**: 17
- **API Documentation**: SpringDoc OpenAPI 2.0.2

### Core Services
1. **Authentication Service** (`AuthService`)
   - User registration
   - User login
   - JWT token management
   - Token refresh

2. **Doctor Service** (`DoctorService`)
   - Doctor profile management
   - Doctor listing and search

3. **Appointment Service** (`AppointmentService`)
   - Appointment booking
   - Appointment management

4. **User Details Service** (`CustomUserDetailsService`)
   - User authentication
   - User details management

### API Endpoints

#### Authentication
```
POST /api/auth/register    # User registration
POST /api/auth/login       # User login
POST /api/auth/refresh     # Refresh token
```

#### Doctors
```
GET    /api/doctors        # List all doctors
GET    /api/doctors/{id}   # Get doctor details
POST   /api/doctors        # Add new doctor
```

#### Appointments
```
POST   /api/appointments           # Book appointment
GET    /api/appointments/{userId}  # Get user appointments
PUT    /api/appointments/{id}      # Update appointment
DELETE /api/appointments/{id}      # Cancel appointment
```

## Future Implementations

### Caching Strategy
1. **Planned Implementation**
   - Redis for distributed caching
   - Cache for doctor listings
   - Cache for user sessions
   - Cache for frequently accessed data

2. **Caching Policies**
   - Time-based expiration
   - LRU eviction policy
   - Cache invalidation on updates

### Kubernetes Deployment
1. **Helm Charts Structure**
   ```
   medimeet/
   ├── Chart.yaml
   ├── values.yaml
   ├── templates/
   │   ├── deployment.yaml
   │   ├── service.yaml
   │   ├── ingress.yaml
   │   └── configmap.yaml
   ```

2. **Pod Configuration**
   - Resource limits and requests
   - Health checks
   - Rolling updates
   - Auto-scaling policies

3. **Scaling Strategy**
   - Horizontal Pod Autoscaling (HPA)
   - Custom metrics scaling
   - Load balancing configuration

### API Gateway (To be implemented)
1. **Features**
   - Rate limiting
   - Request routing
   - Authentication/Authorization
   - Request/Response transformation
   - Circuit breaking
   - API versioning

2. **Technologies to Consider**
   - Spring Cloud Gateway
   - Kong
   - Nginx Ingress Controller

### Monitoring and Logging
1. **Metrics**
   - Prometheus integration
   - Grafana dashboards
   - Custom metrics for business KPIs

2. **Logging**
   - ELK Stack integration
   - Structured logging
   - Log aggregation

### Testing Strategy (To be implemented)
1. **Unit Tests**
   - Service layer
   - Repository layer
   - Security components

2. **Integration Tests**
   - API endpoints
   - Database operations
   - Authentication flows

3. **Performance Tests**
   - Load testing
   - Stress testing
   - Endurance testing

### Security Enhancements
1. **Planned Features**
   - Rate limiting
   - IP whitelisting
   - OAuth2 integration
   - Role-based access control (RBAC)
   - API key management
