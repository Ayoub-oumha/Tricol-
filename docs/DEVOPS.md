# DevOps Infrastructure - Tricol Supplier Chain

This document describes the DevOps infrastructure setup for the Tricol Supplier Chain application.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Docker Network                               │
│  ┌─────────┐  ┌─────────┐  ┌───────────┐  ┌──────────┐  ┌─────────┐│
│  │  MySQL  │  │Keycloak │  │   App     │  │SonarQube │  │ Jenkins ││
│  │  :3306  │  │  :8080  │  │  :8081    │  │  :9000   │  │  :8082  ││
│  └────┬────┘  └────┬────┘  └─────┬─────┘  └────┬─────┘  └────┬────┘│
│       │           │              │              │              │     │
│  ┌────┴────┐  ┌───┴────┐        │         ┌────┴────┐              │
│  │mysql_data│  │postgres│        │         │sonar_db │              │
│  └─────────┘  │ _data  │        │         └─────────┘              │
│               └────────┘        │                                    │
└─────────────────────────────────┼────────────────────────────────────┘
                                  │
                            CI/CD Pipeline
```

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose installed
- At least 4GB RAM available (SonarQube requires 2GB+)

### Start All Services

```bash
# Start the complete infrastructure
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Access Points

| Service      | URL                          | Credentials          |
|--------------|------------------------------|----------------------|
| Application  | http://localhost:8081        | -                    |
| Swagger UI   | http://localhost:8081/swagger-ui.html | -           |
| Keycloak     | http://localhost:8080        | admin / admin        |
| SonarQube    | http://localhost:9000        | admin / admin        |
| Jenkins      | http://localhost:8082        | See initial password |

## 📦 Docker Configuration

### Dockerfile Features
- Multi-stage build for optimized image size
- Non-root user for security
- Health check endpoint
- JVM container optimizations
- Environment variable support

### Services in docker-compose.yml

| Service     | Image                              | Port  | Purpose                    |
|-------------|------------------------------------|-------|----------------------------|
| mysql       | mysql:8.0                          | 3306  | Application database       |
| postgres    | postgres:15                        | 5432  | Keycloak database          |
| keycloak    | quay.io/keycloak/keycloak:24.0.3   | 8080  | Identity management        |
| app         | Built from Dockerfile              | 8081  | Spring Boot application    |
| sonar-db    | postgres:15                        | -     | SonarQube database         |
| sonarqube   | sonarqube:lts-community            | 9000  | Code quality analysis      |
| jenkins     | jenkins/jenkins:lts                | 8082  | CI/CD pipeline             |

## 🔄 CI/CD Pipeline (Jenkins)

### Pipeline Stages

```
1. Checkout       → Clone source code
2. Build          → mvn clean compile
3. Unit Tests     → mvn test
4. Code Coverage  → JaCoCo report generation
5. SonarQube      → Static code analysis
6. Quality Gate   → Pass/fail based on thresholds
7. Package        → mvn package (JAR)
8. Docker Build   → Build container image
9. Docker Push    → Push to registry (main branch)
10. Deploy        → Deploy to environment (main branch)
```

### Jenkins Initial Setup

1. Get initial admin password:
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

2. Access Jenkins at http://localhost:8082

3. Install suggested plugins + additional:
   - Docker Pipeline
   - SonarQube Scanner
   - JaCoCo

4. Configure tools in Jenkins:
   - Maven: `Maven-3.9`
   - JDK: `JDK-17`

5. Configure SonarQube server:
   - Name: `SonarQube`
   - URL: `http://sonarqube:9000`

6. Add credentials:
   - `dockerhub-credentials` (Docker Hub)
   - `sonarqube-token` (SonarQube token)

## 📊 SonarQube Configuration

### Initial Setup

1. Access http://localhost:9000 (default: admin/admin)
2. Change admin password
3. Create project: `tricol-supplier-chain`
4. Generate authentication token
5. Configure quality gate thresholds

### Quality Gate Rules (Recommended)

| Metric                     | Threshold |
|----------------------------|-----------|
| Coverage on new code       | ≥ 80%     |
| Duplicated lines           | ≤ 3%      |
| Maintainability rating     | A         |
| Reliability rating         | A         |
| Security rating            | A         |
| Blocker issues             | 0         |

### Running Analysis Locally

```bash
# Run tests with coverage
./mvnw clean test

# Run SonarQube analysis
./mvnw sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<your-token>
```

## 📁 Files Overview

| File                                      | Purpose                           |
|-------------------------------------------|-----------------------------------|
| `Dockerfile`                              | Application container definition  |
| `docker-compose.yml`                      | Multi-service orchestration       |
| `Jenkinsfile`                             | CI/CD pipeline definition         |
| `sonar-project.properties`                | SonarQube project configuration   |
| `pom.xml`                                 | Maven build with JaCoCo/Surefire  |
| `src/main/resources/application-docker.properties` | Docker environment config |
| `src/test/resources/application-test.properties`   | Test environment config   |

## 🧪 Testing

### Run Tests Locally

```bash
# Run all tests
./mvnw test

# Run with test profile
./mvnw test -Dspring.profiles.active=test

# Generate coverage report
./mvnw jacoco:report
# Report: target/site/jacoco/index.html
```

### Coverage Report

After running tests, the JaCoCo coverage report is available at:
`target/site/jacoco/index.html`

## 🔧 Environment Profiles

| Profile  | File                           | Use Case              |
|----------|--------------------------------|-----------------------|
| default  | application.properties         | Local development     |
| docker   | application-docker.properties  | Docker containers     |
| test     | application-test.properties    | Unit/Integration tests|

## ⚠️ Production Considerations

1. **Security**
   - Change all default passwords
   - Use secrets management (Docker secrets, Vault)
   - Enable HTTPS/TLS

2. **SonarQube**
   - Increase memory: `vm.max_map_count=262144`
   - Use external PostgreSQL in production

3. **Jenkins**
   - Configure backup for jenkins_home volume
   - Use Jenkins agents for builds
   - Implement proper access control

4. **Monitoring**
   - Add Prometheus/Grafana for metrics
   - Configure log aggregation (ELK/Loki)
