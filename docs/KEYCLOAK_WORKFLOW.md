# Keycloak Authentication Workflow

## Quick Start Guide

This guide walks you through setting up and using Keycloak authentication with the Tricol Supplier Chain application.

---

## 📋 Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Start Keycloak Server](#2-start-keycloak-server)
3. [Configure Keycloak Realm](#3-configure-keycloak-realm)
4. [Create Client Application](#4-create-client-application)
5. [Setup Roles](#5-setup-roles)
6. [Create Users](#6-create-users)
7. [Get Access Token](#7-get-access-token)
8. [Access Protected APIs](#8-access-protected-apis)
9. [Token Refresh Flow](#9-token-refresh-flow)

---

## 1. Prerequisites

- ✅ Docker & Docker Compose installed
- ✅ Application running on `http://localhost:8081`
- ✅ Postman or cURL for testing

---

## 2. Start Keycloak Server

### Option A: Using Docker Compose (Recommended)

```bash
cd x/
docker-compose up -d postgres keycloak
```

### Option B: Standalone Docker

```bash
# Start PostgreSQL for Keycloak
docker run -d --name keycloak_db \
  -e POSTGRES_DB=keycloak \
  -e POSTGRES_USER=keycloak \
  -e POSTGRES_PASSWORD=keycloak \
  -p 5432:5432 \
  postgres:15

# Start Keycloak
docker run -d --name keycloak \
  -e KC_DB=postgres \
  -e KC_DB_URL=jdbc:postgresql://host.docker.internal:5432/keycloak \
  -e KC_DB_USERNAME=keycloak \
  -e KC_DB_PASSWORD=keycloak \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -p 8080:8080 \
  quay.io/keycloak/keycloak:24.0.3 start-dev
```

### Verify Keycloak is Running

Open browser: **http://localhost:8080**

Login to Admin Console:
- **Username**: `admin`
- **Password**: `admin`

---

## 3. Configure Keycloak Realm

### Step 3.1: Create New Realm

1. Click dropdown arrow next to **"master"** (top-left)
2. Click **"Create Realm"**
3. Fill in:
   - **Realm name**: `Tricol`
4. Click **"Create"**

![Create Realm](https://placeholder-for-screenshot)

### Step 3.2: Verify Realm Settings

1. Go to **Realm Settings** → **General**
2. Ensure:
   - **Enabled**: ON
   - **User-managed access**: ON (optional)
3. Click **Save**

---

## 4. Create Client Application

### Step 4.1: Create New Client

1. Navigate to: **Clients** → **Create client**

2. **General Settings**:
   ```
   Client type: OpenID Connect
   Client ID: supplier-chain-api
   Name: Supplier Chain API
   Description: Backend API Client
   ```
   Click **Next**

3. **Capability Config**:
   ```
   Client authentication: OFF (Public client)
   Authorization: OFF
   
   Authentication flow:
   ✅ Standard flow
   ✅ Direct access grants (Required for password grant)
   ```
   Click **Next**

4. **Login Settings**:
   ```
   Root URL: http://localhost:8081
   Home URL: http://localhost:8081
   Valid redirect URIs: http://localhost:8081/*
   Valid post logout redirect URIs: http://localhost:8081/*
   Web origins: http://localhost:8081
   ```
   Click **Save**

### Step 4.2: Note Down Client Details

After creation, note:
- **Client ID**: `supplier-chain-api`
- **Realm**: `Tricol`

---

## 5. Setup Roles

### Step 5.1: Create Realm Roles

1. Navigate to: **Realm roles** → **Create role**

2. Create these roles (one by one):

| Role Name | Description |
|-----------|-------------|
| `ROLE_ADMIN` | Full system administrator |
| `ROLE_RESPONSABLE_ACHATS` | Purchasing Manager |
| `ROLE_MAGASINIER` | Warehouse Manager |
| `ROLE_CHEF_ATELIER` | Workshop Manager |

For each role:
```
Role name: ROLE_ADMIN
Description: Full system administrator access
```
Click **Save**

### Step 5.2: Verify Roles

Go to **Realm roles** and confirm all 4 roles exist.

---

## 6. Create Users

### Step 6.1: Create Admin User

1. Navigate to: **Users** → **Add user**

2. **User Details**:
   ```
   Username: admin_user
   Email: admin@tricol.com
   Email verified: ON
   First name: Admin
   Last name: User
   Enabled: ON
   ```
   Click **Create**

3. **Set Password**:
   - Go to **Credentials** tab
   - Click **Set password**
   ```
   Password: admin123
   Password confirmation: admin123
   Temporary: OFF
   ```
   Click **Save** → **Save password**

4. **Assign Role**:
   - Go to **Role mapping** tab
   - Click **Assign role**
   - Select **ROLE_ADMIN**
   - Click **Assign**

### Step 6.2: Create Other Users

Repeat for other roles:

| Username | Email | Password | Role |
|----------|-------|----------|------|
| `chef_atelier` | chef@tricol.com | `chef123` | ROLE_CHEF_ATELIER |
| `magasinier` | mag@tricol.com | `mag123` | ROLE_MAGASINIER |
| `responsable` | resp@tricol.com | `resp123` | ROLE_RESPONSABLE_ACHATS |

---

## 7. Get Access Token

### Method 1: Using cURL

```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=supplier-chain-api" \
  -d "grant_type=password" \
  -d "username=admin_user" \
  -d "password=admin123"
```

### Method 2: Using PowerShell

```powershell
$body = @{
    client_id = "supplier-chain-api"
    grant_type = "password"
    username = "admin_user"
    password = "admin123"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" `
    -Method POST `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $body

$response.access_token
```

### Method 3: Using Postman

1. Create new **POST** request
2. URL: `http://localhost:8080/realms/Tricol/protocol/openid-connect/token`
3. Body → **x-www-form-urlencoded**:
   | Key | Value |
   |-----|-------|
   | client_id | supplier-chain-api |
   | grant_type | password |
   | username | admin_user |
   | password | admin123 |

4. Click **Send**

### Expected Response

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ...",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC...",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "abc123...",
    "scope": "email profile"
}
```

### Decode Token (Optional)

Go to [jwt.io](https://jwt.io) and paste your `access_token` to see claims:

```json
{
  "sub": "929c81af-15ad-4268-ba79-f5889dff3e69",
  "preferred_username": "admin_user",
  "email": "admin@tricol.com",
  "realm_access": {
    "roles": [
      "ROLE_ADMIN",
      "offline_access",
      "uma_authorization"
    ]
  }
}
```

---

## 8. Access Protected APIs

### Using the Access Token

Copy the `access_token` from step 7 and use it in your API requests.

### Example: Get Products

**cURL**:
```bash
curl -X GET "http://localhost:8081/api/v1/produits" \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOi..."
```

**PowerShell**:
```powershell
$token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOi..."

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/produits" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
```

**Postman**:
1. Create **GET** request to `http://localhost:8081/api/v1/produits`
2. Go to **Authorization** tab
3. Type: **Bearer Token**
4. Token: Paste your access_token
5. Click **Send**

### Expected Response (200 OK)

```json
{
    "content": [
        {
            "id": 1,
            "nom": "Product A",
            "reference": "PROD-001"
        }
    ],
    "totalElements": 1
}
```

---

## 9. Token Refresh Flow

Access tokens expire quickly (default 5 minutes). Use refresh token to get new access token.

### Refresh Token Request

```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=supplier-chain-api" \
  -d "grant_type=refresh_token" \
  -d "refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC..."
```

### Response

```json
{
    "access_token": "NEW_ACCESS_TOKEN...",
    "expires_in": 300,
    "refresh_token": "NEW_REFRESH_TOKEN...",
    "token_type": "Bearer"
}
```

---

## 🔄 Complete Authentication Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         KEYCLOAK AUTHENTICATION FLOW                         │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────┐                    ┌──────────────┐                    ┌──────────┐
│  Client  │                    │   Keycloak   │                    │ Backend  │
│(Postman) │                    │   Server     │                    │   API    │
└────┬─────┘                    └──────┬───────┘                    └────┬─────┘
     │                                 │                                 │
     │  1. POST /token                 │                                 │
     │     grant_type=password         │                                 │
     │     username=admin_user         │                                 │
     │     password=admin123           │                                 │
     ├────────────────────────────────>│                                 │
     │                                 │                                 │
     │                                 │ Validate credentials            │
     │                                 │ Generate JWT tokens             │
     │                                 │                                 │
     │  2. Response                    │                                 │
     │     access_token (5min)         │                                 │
     │     refresh_token (30min)       │                                 │
     │<────────────────────────────────┤                                 │
     │                                 │                                 │
     │  3. GET /api/v1/produits                                          │
     │     Authorization: Bearer <access_token>                          │
     ├──────────────────────────────────────────────────────────────────>│
     │                                 │                                 │
     │                                 │  4. Validate JWT                │
     │                                 │     Fetch JWKS from Keycloak    │
     │                                 │<────────────────────────────────┤
     │                                 │                                 │
     │                                 │  5. Return public keys          │
     │                                 ├────────────────────────────────>│
     │                                 │                                 │
     │                                 │     Verify signature ✓          │
     │                                 │     Check expiration ✓          │
     │                                 │     Extract roles               │
     │                                 │     Check permissions           │
     │                                 │                                 │
     │  6. Response: 200 OK                                              │
     │     { products: [...] }                                           │
     │<──────────────────────────────────────────────────────────────────┤
     │                                 │                                 │
     │  ─ ─ ─ ─ ─ TOKEN EXPIRES ─ ─ ─ ─                                  │
     │                                 │                                 │
     │  7. POST /token                 │                                 │
     │     grant_type=refresh_token    │                                 │
     │     refresh_token=<token>       │                                 │
     ├────────────────────────────────>│                                 │
     │                                 │                                 │
     │  8. New tokens                  │                                 │
     │<────────────────────────────────┤                                 │
     │                                 │                                 │
```

---

## 🔗 API Endpoints Summary

### Keycloak Endpoints

| Endpoint | Purpose |
|----------|---------|
| `POST /realms/Tricol/protocol/openid-connect/token` | Get/Refresh tokens |
| `GET /realms/Tricol/.well-known/openid-configuration` | OpenID Config |
| `GET /realms/Tricol/protocol/openid-connect/certs` | JWKS (Public Keys) |
| `POST /realms/Tricol/protocol/openid-connect/logout` | Logout |
| `GET /realms/Tricol/protocol/openid-connect/userinfo` | User Info |

### Application Endpoints

| Method | Endpoint | Required Role |
|--------|----------|---------------|
| GET | `/api/v1/produits` | Any authenticated |
| POST | `/api/v1/produits` | ADMIN, RESPONSABLE_ACHATS |
| GET | `/api/v1/fournisseurs` | Any authenticated |
| POST | `/api/v1/commandes` | ADMIN, RESPONSABLE_ACHATS |
| GET | `/api/v1/stocks` | Any authenticated |
| POST | `/api/v1/bons-sortie` | ADMIN, MAGASINIER, CHEF_ATELIER |

---

## 🛠️ Troubleshooting

### Error: "Invalid user credentials"

**Cause**: Wrong username or password
**Solution**: 
1. Verify user exists in Keycloak
2. Check password is correct
3. Ensure user is enabled

### Error: "Client not found"

**Cause**: Wrong client_id
**Solution**: 
1. Verify client_id is `supplier-chain-api`
2. Check client exists in Tricol realm

### Error: 401 Unauthorized on API

**Cause**: Invalid or expired token
**Solution**:
1. Get new token from Keycloak
2. Ensure using correct realm (`Tricol`)
3. Check token hasn't expired

### Error: 403 Forbidden on API

**Cause**: User lacks required permissions
**Solution**:
1. Check user has correct role in Keycloak
2. Verify role name matches (e.g., `ROLE_ADMIN`)
3. Delete user from app database and re-login

### Error: "Connection refused" to Keycloak

**Cause**: Keycloak not running
**Solution**:
```bash
docker ps  # Check if keycloak container is running
docker-compose up -d keycloak  # Start if not running
```

---

## 📝 Quick Reference Card

```
┌─────────────────────────────────────────────────────────────────┐
│                    KEYCLOAK QUICK REFERENCE                      │
├─────────────────────────────────────────────────────────────────┤
│ Admin Console:     http://localhost:8080                        │
│ Admin Login:       admin / admin                                │
│                                                                 │
│ Realm:             Tricol                                       │
│ Client ID:         supplier-chain-api                           │
│                                                                 │
│ Token URL:                                                      │
│ http://localhost:8080/realms/Tricol/protocol/openid-connect/token│
│                                                                 │
│ Token Request (Password Grant):                                 │
│   client_id=supplier-chain-api                                  │
│   grant_type=password                                           │
│   username=<user>                                               │
│   password=<pass>                                               │
│                                                                 │
│ API Usage:                                                      │
│   Authorization: Bearer <access_token>                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ Checklist

- [ ] Keycloak running on port 8080
- [ ] Realm "Tricol" created
- [ ] Client "supplier-chain-api" configured
- [ ] Roles created (ROLE_ADMIN, ROLE_CHEF_ATELIER, etc.)
- [ ] Users created and roles assigned
- [ ] Successfully obtained access token
- [ ] Successfully called protected API

---

**Next Steps**: See [KEYCLOAK_AUTH_FLOW.md](./KEYCLOAK_AUTH_FLOW.md) for detailed architecture and troubleshooting.

