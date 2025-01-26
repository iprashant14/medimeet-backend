# MediMeet Deployment Guide

## Overview
This document outlines the deployment strategy for MediMeet using OpenShift, including API Gateway configuration, secure storage, and GitLab CI/CD pipeline setup.

## Deployment Architecture

### Components
1. Frontend (Flutter Web)
2. Backend (Spring Boot)
3. MongoDB Database
4. API Gateway (Red Hat 3scale)
5. Secret Management (HashiCorp Vault)

## OpenShift Configuration

### Prerequisites
- OpenShift 4.x cluster
- GitLab account with CI/CD capabilities
- HashiCorp Vault instance
- MongoDB Atlas account (or self-hosted MongoDB)

### Environment Setup

1. **Namespaces**
```bash
# Create namespaces for different environments
oc create namespace medimeet-dev
oc create namespace medimeet-stage
oc create namespace medimeet-prod
```

2. **Secrets Management**
```yaml
# vault-config.yaml
apiVersion: vault.hashicorp.com/v1alpha1
kind: VaultSecret
metadata:
  name: medimeet-secrets
spec:
  vaultRole: medimeet-role
  secrets:
    - path: secret/medimeet
      keys:
        - jwt-secret
        - mongodb-uri
        - google-client-id
```

### Deployment Configurations

1. **Backend Deployment**
```yaml
# backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: medimeet-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: medimeet-backend
  template:
    metadata:
      labels:
        app: medimeet-backend
    spec:
      containers:
      - name: medimeet-backend
        image: ${BACKEND_IMAGE}
        ports:
        - containerPort: 8080
        envFrom:
        - secretRef:
            name: medimeet-secrets
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

2. **Frontend Deployment**
```yaml
# frontend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: medimeet-frontend
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: medimeet-frontend
        image: ${FRONTEND_IMAGE}
        ports:
        - containerPort: 80
```

3. **API Gateway Configuration**
```yaml
# api-gateway.yaml
apiVersion: 3scale.net/v1alpha1
kind: APIManager
metadata:
  name: medimeet-gateway
spec:
  wildcardDomain: apps.example.com
  resourceRequirementsEnabled: true
```

### Horizontal Pod Autoscaling

1. **Backend HPA**
```yaml
# backend-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: medimeet-backend-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: medimeet-backend
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 80
```

## Environment-Specific Configurations

### Development Environment
```yaml
# values-dev.yaml
environment: development
replicas:
  backend: 1
  frontend: 1
resources:
  backend:
    requests:
      memory: "512Mi"
      cpu: "500m"
    limits:
      memory: "1Gi"
      cpu: "1000m"
monitoring:
  enabled: true
```

### Staging Environment
```yaml
# values-stage.yaml
environment: staging
replicas:
  backend: 2
  frontend: 2
resources:
  backend:
    requests:
      memory: "1Gi"
      cpu: "1000m"
    limits:
      memory: "2Gi"
      cpu: "2000m"
monitoring:
  enabled: true
```

### Production Environment
```yaml
# values-prod.yaml
environment: production
replicas:
  backend: 3
  frontend: 3
resources:
  backend:
    requests:
      memory: "2Gi"
      cpu: "2000m"
    limits:
      memory: "4Gi"
      cpu: "4000m"
monitoring:
  enabled: true
```

## GitLab CI/CD Pipeline

```yaml
# .gitlab-ci.yml
stages:
  - test
  - build
  - deploy

variables:
  DOCKER_REGISTRY: "registry.example.com"
  
.openshift_template: &openshift_template
  image: openshift/origin-cli
  before_script:
    - oc login --token=$OPENSHIFT_TOKEN --server=$OPENSHIFT_SERVER

test:
  stage: test
  script:
    - ./gradlew test

build_backend:
  stage: build
  script:
    - ./gradlew build
    - docker build -t $DOCKER_REGISTRY/medimeet-backend .
    - docker push $DOCKER_REGISTRY/medimeet-backend

deploy_dev:
  <<: *openshift_template
  stage: deploy
  script:
    - helm upgrade --install medimeet ./helm -f values-dev.yaml
  environment:
    name: development
  only:
    - develop

deploy_stage:
  <<: *openshift_template
  stage: deploy
  script:
    - helm upgrade --install medimeet ./helm -f values-stage.yaml
  environment:
    name: staging
  only:
    - staging

deploy_prod:
  <<: *openshift_template
  stage: deploy
  script:
    - helm upgrade --install medimeet ./helm -f values-prod.yaml
  environment:
    name: production
  only:
    - main
  when: manual
```

## Monitoring and Scaling

### Prometheus Configuration
```yaml
# prometheus.yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: medimeet-backend
spec:
  endpoints:
  - port: http
  selector:
    matchLabels:
      app: medimeet-backend
```

### Grafana Dashboard
- JVM metrics
- API response times
- Error rates
- Resource utilization

## Backup Strategy

1. **Database Backup**
   - Daily automated backups
   - Point-in-time recovery
   - Geo-redundant storage

2. **Application State**
   - ConfigMap versioning
   - Secret versioning
   - Persistent volume backups

## Security Considerations

1. **Network Policies**
```yaml
# network-policy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: medimeet-backend-policy
spec:
  podSelector:
    matchLabels:
      app: medimeet-backend
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: api-gateway
```

2. **Pod Security Policies**
   - Non-root containers
   - Read-only root filesystem
   - Limited capabilities

## Rollback Procedures

1. **Immediate Rollback**
```bash
# Roll back to previous version
oc rollout undo deployment/medimeet-backend
```

2. **Version-Specific Rollback**
```bash
# Roll back to specific version
oc rollout undo deployment/medimeet-backend --to-revision=2
```
