# Microservices Lab with Kubernetes

This project implements a microservices architecture with JWT authentication, consisting of:
- Authorization Server
- API Gateway
- Joke Service
- Quote Service

## Prerequisites
- Docker Desktop with Kubernetes enabled
- kubectl CLI
- Maven
- Postman or similar API testing tool

## Building the Services
```bash
# Build all services
./mvnw clean package spring-boot:build-image
```

## Deploying to Kubernetes

1. Start Kubernetes:
   - Ensure Docker Desktop is running with Kubernetes enabled
   - Verify cluster is running: `kubectl cluster-info`

2. Deploy the services:
```bash
kubectl apply -f k8s/
```

3. Verify deployments:
```bash
kubectl get pods
kubectl get services
kubectl get ingress
```

## Testing the System

1. Get the Ingress IP:
```bash
kubectl get ingress
```

2. Test the authentication flow:
   - POST to `http://<ingress-ip>/api/auth/login` with credentials
   - You'll receive a JWT token

3. Test protected endpoints:
   - Add the JWT token to the Authorization header: `Bearer <your-token>`
   - GET `http://<ingress-ip>/api/jokes/random`
   - GET `http://<ingress-ip>/api/quotes/random`

## Architecture Improvements

1. **Token Management**:
   - Implement token refresh mechanism
   - Add token revocation capability
   - Store tokens in Redis for better scalability

2. **Service Discovery**:
   - Implement Kubernetes Service Discovery
   - Add health checks and circuit breakers
   - Use ConfigMaps for service configuration

3. **Security Enhancements**:
   - Add rate limiting
   - Implement CORS policies
   - Add request validation

4. **Monitoring and Logging**:
   - Add Prometheus metrics
   - Implement distributed tracing
   - Centralized logging with ELK stack

## Cleanup
```bash
kubectl delete -f k8s/
``` 