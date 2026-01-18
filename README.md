# Tricol Supplier Chain Management System

A Spring Boot 3.5 application for supplier chain management with **dual authentication** supporting both **Local JWT** and **Keycloak OAuth2** integration.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Keycloak Setup](#keycloak-setup)
  - [Docker Deployment](#docker-deployment)
  - [Realm Configuration](#realm-configuration)
  - [Client Configuration](#client-configuration)
  - [Roles & Users Setup](#roles--users-setup)
- [Application Configuration](#application-configuration)
- [Authentication Flow](#authentication-flow)
- [API Endpoints](#api-endpoints)
- [Running the Application](#running-the-application)
- [Testing Authentication](#testing-authentication)

---

## Overview

This application implements a **supplier chain management system** with comprehensive security features:

- **Dual Authentication**: Supports both local JWT tokens and Keycloak SSO tokens
- **Role-Based Access Control (RBAC)**: Fine-grained permissions for different user roles
- **Audit Logging**: Tracks all user actions for compliance
- **FIFO Stock Management**: Complete inventory tracking with lot management

### Available Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full system access |
| `RESPONSABLE_ACHATS` | Purchasing Manager - manages suppliers and orders |
| `MAGASINIER` | Warehouse Manager - handles stock and deliveries |
| `CHEF_ATELIER` | Workshop Manager - manages stock consumption |

---

## Tech Stack

- **Backend**: Spring Boot 3.5.7, Java 17
- **Security**: Spring Security 6, OAuth2 Resource Server, JWT
- **Identity Provider**: Keycloak 24.0.3
- **Database**: MySQL 8.0 / PostgreSQL 15
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build**: Maven
- **Containerization**: Docker & Docker Compose

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0 or PostgreSQL 15

---

## Keycloak Setup

### Docker Deployment

Start Keycloak with PostgreSQL using Docker Compose:

```bash
cd x/
docker-compose up -d postgres keycloak
```

This will start:
- **PostgreSQL** on port `5432` (Keycloak database)
- **Keycloak** on port `8080`

Access Keycloak Admin Console:
- **URL**: http://localhost:8080
- **Username**: `admin`
- **Password**: `admin`

### Realm Configuration

1. **Create a new Realm**:
   - Click on dropdown next to "master" realm
   - Click "Create Realm"
   - Name: `Tricol`
   - Click "Create"

2. **Configure Realm Settings**:
   - Go to Realm Settings → General
   - Ensure "Enabled" is ON
   - Save

### Client Configuration

1. **Create OAuth2 Client**:
   - Navigate to: Clients → Create client
   
2. **General Settings**:
   ```
   Client ID: supplier-chain-api
   Name: Supplier Chain API
   Description: Backend API for Tricol Supplier Chain
   ```

3. **Capability Config**:
   ```
   Client authentication: OFF (Public client)
   Authorization: OFF
   Authentication flow: ✓ Standard flow
                       ✓ Direct access grants
   ```

4. **Login Settings**:
   ```
   Root URL: http://localhost:8081
   Home URL: http://localhost:8081
   Valid redirect URIs: http://localhost:8081/*
   Valid post logout redirect URIs: http://localhost:8081/*
   Web origins: http://localhost:8081
   ```

5. **Save the client**

### Roles & Users Setup

#### Create Client Roles

1. Navigate to: Clients → `supplier-chain-api` → Roles
2. Create these roles:

| Role Name | Description |
|-----------|-------------|
| `ADMIN` | Administrator with full access |
| `RESPONSABLE_ACHATS` | Purchasing Manager |
| `MAGASINIER` | Warehouse Manager |
| `CHEF_ATELIER` | Workshop Manager |

#### Create Users

1. Navigate to: Users → Add user
2. Create test users:

**Admin User**:
```
Username: admin
Email: admin@tricol.com
First Name: Admin
Last Name: User
Email Verified: ON
```

3. Set password:
   - Go to Credentials tab
   - Set Password: `admin123`
   - Temporary: OFF

4. Assign roles:
   - Go to Role Mapping tab
   - Click "Assign role"
   - Filter by clients → `supplier-chain-api`
   - Select `ADMIN` role

**Repeat for other users** (responsable, magasinier, chef_atelier)

---

## Application Configuration

### Local Development (application.properties)

```properties
# Server Configuration
spring.application.name=Brief6-tricol-supplier-chain
server.port=8081

# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/all_tricol?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration (Local Auth)
security.jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
security.jwt.access-token-expiration=86400000
security.jwt.refresh-token-expiration=604800000

# Keycloak Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://127.0.0.1:8080/realms/Tricol
```

### Docker Environment (application-docker.properties)

```properties
# Keycloak (Docker internal network)
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://host.docker.internal:8080/realms/Tricol
```

---

## Authentication Flow

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CLIENT REQUEST                                  │
│                    Authorization: Bearer <token>                         │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      SPRING SECURITY FILTER CHAIN                        │
├─────────────────────────────────────────────────────────────────────────┤
│  1. JwtAuthenticationFilter (Custom - Local JWT)                        │
│     ├─ Checks if token is local (signed with app secret)                │
│     ├─ Validates via JwtService                                         │
│     └─ Loads user from DB via CustomUserDetailsService                  │
│                                                                          │
│  2. OAuth2ResourceServerFilter (Spring - Keycloak JWT)                  │
│     ├─ ConditionalBearerTokenResolver (skips if already authenticated)  │
│     ├─ Validates JWT signature via Keycloak JWKS                        │
│     ├─ Verifies issuer: http://127.0.0.1:8080/realms/Tricol            │
│     └─ Converts JWT to Authentication via jwtAuthenticationConverter()  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTHORIZATION LAYER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│  @RequirePermission Annotation                                           │
│  └─ PermissionAspect intercepts method calls                            │
│      ├─ Gets current user via CurrentUserService                        │
│      ├─ Checks permission via PermissionService                         │
│      └─ Throws InsufficientPermissionsException if denied               │
└─────────────────────────────────────────────────────────────────────────┘
```

### How Dual Authentication Works

1. **Request arrives** with `Authorization: Bearer <token>` header

2. **JwtAuthenticationFilter** (runs first):
   - Attempts to validate token as a **local JWT**
   - If valid → sets authentication and skips Keycloak validation
   - If invalid/not local → passes to next filter

3. **OAuth2 Resource Server** (Keycloak):
   - `ConditionalBearerTokenResolver` checks if already authenticated
   - If not → validates token against Keycloak's JWKS endpoint
   - Extracts roles from `resource_access.supplier-chain-api.roles`

### Token Validation

**Keycloak Token Validation**:
```
JWKS URL: http://127.0.0.1:8080/realms/Tricol/protocol/openid-connect/certs
Issuer: http://127.0.0.1:8080/realms/Tricol
```

**Local Token Validation**:
```
Algorithm: HS256
Secret: Configured in security.jwt.secret
```

---

## API Endpoints

### Authentication Endpoints (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login (local auth) |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Logout user |

### Protected Endpoints

| Method | Endpoint | Required Permission |
|--------|----------|---------------------|
| GET | `/api/v1/fournisseurs` | `FOURNISSEUR_READ` |
| POST | `/api/v1/fournisseurs` | `FOURNISSEUR_CREATE` |
| GET | `/api/v1/produits` | `PRODUIT_READ` |
| POST | `/api/v1/produits` | `PRODUIT_CREATE` |
| GET | `/api/v1/commandes` | `COMMANDE_READ` |
| POST | `/api/v1/commandes` | `COMMANDE_CREATE` |
| GET | `/api/v1/stocks` | `STOCK_READ` |
| GET | `/api/v1/bons-sortie` | `BON_SORTIE_READ` |
| POST | `/api/v1/bons-sortie` | `BON_SORTIE_CREATE` |

### Swagger UI

Access API documentation at: http://localhost:8081/swagger-ui.html

---

## Running the Application

### Option 1: Local Development

1. **Start MySQL**:
   ```bash
   # Ensure MySQL is running on port 3306
   ```

2. **Start Keycloak**:
   ```bash
   cd x/
   docker-compose up -d postgres keycloak
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Option 2: Full Docker Deployment

```bash
cd x/
docker-compose up -d
```

This starts:
- MySQL (port 3306)
- PostgreSQL for Keycloak (port 5432)
- Keycloak (port 8080)
- Spring Boot App (port 8081)

---

## Testing Authentication

### Get Keycloak Token

```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=supplier-chain-api" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123"
```

**Response**:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

### Use Token to Access API

```bash
curl -X GET "http://localhost:8081/api/v1/produits" \
  -H "Authorization: Bearer <access_token>"
```

### Local Authentication

**Register**:
```bash
curl -X POST "http://localhost:8081/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Login**:
```bash
curl -X POST "http://localhost:8081/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

---

## Troubleshooting

### Common Issues

1. **Keycloak Connection Refused**
   - Ensure Keycloak is running: `docker ps`
   - Check if port 8080 is accessible
   - Verify `issuer-uri` matches your Keycloak realm URL

2. **Invalid Token**
   - Check token expiration
   - Verify client ID matches in Keycloak
   - Ensure roles are properly assigned

3. **Database Connection**
   - Check database is running
   - Verify connection string and credentials
   - Ensure database exists

### Debug Mode

Enable security debug logging:
```properties
logging.level.org.springframework.security=TRACE
```

---

## Project Structure

```
src/main/java/org/tricol/supplierchain/
├── config/
│   ├── SecurityConfig.java       # Security filter chain configuration
│   ├── DataSeeder.java           # Initial data seeding
│   └── JacksonConfig.java
├── controller/
│   ├── AuthController.java       # Authentication endpoints
│   ├── FournisseurController.java
│   ├── ProduitController.java
│   └── ...
├── security/
│   ├── JwtAuthenticationFilter.java      # Local JWT filter
│   ├── JwtService.java                   # JWT operations
│   ├── ConditionalBearerTokenResolver.java # Keycloak token resolver
│   ├── CustomUserDetailsService.java
│   ├── PermissionAspect.java             # Permission checking
│   └── RequirePermission.java            # Permission annotation
├── entity/
│   ├── UserApp.java
│   ├── Role.java
│   ├── Permission.java
│   └── ...
└── service/
    ├── CurrentUserService.java   # Get current authenticated user
    ├── PermissionService.java    # Permission management
    └── ...
```

---

## License

This project is part of the Tricol educational program.

---

## Authors

Tricol Development Team

