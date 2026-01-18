# Keycloak Authentication & Authorization Flow - Complete Analysis

## Table of Contents
1. [Overview](#overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Request Flow Sequence](#request-flow-sequence)
4. [Component Details](#component-details)
5. [Configuration Files](#configuration-files)
6. [Current Issue Analysis](#current-issue-analysis)

---

## Overview

This application implements a **dual authentication system**:
- **Local JWT Authentication**: For users registered directly in the application database
- **Keycloak OAuth2 Authentication**: For users authenticated via Keycloak SSO

Both systems coexist, with Spring Security handling token validation and custom permission-based authorization.

---

## Architecture Diagram

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
│                                                                           │
│  2. OAuth2ResourceServerFilter (Spring - Keycloak JWT)                  │
│     ├─ ConditionalBearerTokenResolver (skips if already authenticated)  │
│     ├─ Validates JWT signature via Keycloak JWKS                        │
│     ├─ Verifies issuer: http://127.0.0.1:8080/realms/Tricol            │
│     └─ Converts JWT to Authentication via jwtAuthenticationConverter()  │
│                                                                           │
│  3. ExceptionHandling                                                    │
│     └─ JwtAuthenticationEntryPoint (returns 401 JSON response)          │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTHORIZATION LAYER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│  URL-Based Security (SecurityConfig)                                     │
│  ├─ /api/v1/auth/** → permitAll()                                       │
│  ├─ /swagger-ui/** → permitAll()                                        │
│  ├─ /actuator/health → permitAll()                                      │
│  └─ /** → authenticated()                                               │
│                                                                           │
│  Method-Level Security (AOP)                                             │
│  └─ @RequirePermission("PRODUIT_READ")                                  │
│      └─ PermissionAspect intercepts                                     │
│          ├─ Gets current user via CurrentUserService                    │
│          ├─ Checks permission via PermissionService                     │
│          └─ Throws InsufficientPermissionsException if denied           │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         CONTROLLER                                       │
│                    (e.g., ProduitController)                             │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Request Flow Sequence

### Scenario 1: Keycloak User First Login

```
┌──────┐          ┌──────────┐          ┌─────────────┐          ┌──────────┐
│Client│          │Spring    │          │CurrentUser  │          │Database  │
│      │          │Security  │          │Service      │          │          │
└──┬───┘          └────┬─────┘          └──────┬──────┘          └────┬─────┘
   │                   │                       │                      │
   │ GET /api/v1/produits                      │                      │
   │ Bearer <keycloak-token>                   │                      │
   ├──────────────────>│                       │                      │
   │                   │                       │                      │
   │              [1] JwtAuthenticationFilter  │                      │
   │                   │ isLocalToken()?       │                      │
   │                   │ → false (not signed   │                      │
   │                   │    with app secret)   │                      │
   │                   │ → skip                │                      │
   │                   │                       │                      │
   │              [2] OAuth2ResourceServerFilter                      │
   │                   │ Fetch JWKS from       │                      │
   │                   │ Keycloak              │                      │
   │                   │ Verify signature ✓    │                      │
   │                   │ Verify issuer ✓       │                      │
   │                   │ Check expiration ✓    │                      │
   │                   │                       │                      │
   │              [3] jwtAuthenticationConverter()                    │
   │                   │ Extract authorities   │                      │
   │                   │ from JWT claims:      │                      │
   │                   │ resource_access       │                      │
   │                   │  .supplier-chain-api  │                      │
   │                   │  .roles               │                      │
   │                   │ → ["ROLE_CHEF_ATELIER"]                      │
   │                   │                       │                      │
   │                   │ Create JwtAuthenticationToken                │
   │                   │ Set in SecurityContext│                      │
   │                   │                       │                      │
   │              [4] URL Authorization        │                      │
   │                   │ /api/v1/produits      │                      │
   │                   │ → authenticated() ✓   │                      │
   │                   │                       │                      │
   │              [5] PermissionAspect         │                      │
   │                   │ @RequirePermission    │                      │
   │                   │ ("PRODUIT_READ")      │                      │
   │                   │                       │                      │
   │                   │ getCurrentUser()      │                      │
   │                   ├──────────────────────>│                      │
   │                   │                       │                      │
   │                   │                       │ findByKeycloakUserId │
   │                   │                       │ (jwt.sub)            │
   │                   │                       ├─────────────────────>│
   │                   │                       │                      │
   │                   │                       │ NOT FOUND            │
   │                   │                       │<─────────────────────┤
   │                   │                       │                      │
   │                   │                  [6] createKeycloakUser()    │
   │                   │                       │ extractRoleFromJwt() │
   │                   │                       │ → Looks in:          │
   │                   │                       │   resource_access    │
   │                   │                       │   .supplier-chain-api│
   │                   │                       │   .roles             │
   │                   │                       │ → NULL ❌            │
   │                   │                       │   (WRONG PATH!)      │
   │                   │                       │                      │
   │                   │                       │ INSERT INTO users    │
   │                   │                       │ (role_id = NULL)     │
   │                   │                       ├─────────────────────>│
   │                   │                       │                      │
   │                   │                       │ User created         │
   │                   │                       │<─────────────────────┤
   │                   │                       │                      │
   │                   │ User (role=null)      │                      │
   │                   │<──────────────────────┤                      │
   │                   │                       │                      │
   │              [7] hasPermission(user, "PRODUIT_READ")             │
   │                   │ getUserPermissions()  │                      │
   │                   │ → user.role = null    │                      │
   │                   │ → No role permissions │                      │
   │                   │ → No user permissions │                      │
   │                   │ → EMPTY SET           │                      │
   │                   │                       │                      │
   │                   │ "PRODUIT_READ" in {}? │                      │
   │                   │ → FALSE ❌            │                      │
   │                   │                       │                      │
   │                   │ throw InsufficientPermissionsException       │
   │                   │                       │                      │
   │ 403 Forbidden     │                       │                      │
   │ "Access denied:   │                       │                      │
   │  missing required │                       │                      │
   │  permissions"     │                       │                      │
   │<──────────────────┤                       │                      │
   │                   │                       │                      │
```

### Scenario 2: Local JWT User Login

```
┌──────┐          ┌──────────┐          ┌──────────────┐          ┌──────────┐
│Client│          │Spring    │          │CustomUser    │          │Database  │
│      │          │Security  │          │DetailsService│          │          │
└──┬───┘          └────┬─────┘          └──────┬───────┘          └────┬─────┘
   │                   │                       │                      │
   │ GET /api/v1/produits                      │                      │
   │ Bearer <local-jwt>                        │                      │
   ├──────────────────>│                       │                      │
   │                   │                       │                      │
   │              [1] JwtAuthenticationFilter  │                      │
   │                   │ isLocalToken()?       │                      │
   │                   │ → true (signed with   │                      │
   │                   │    app secret)        │                      │
   │                   │                       │                      │
   │                   │ jwtService            │                      │
   │                   │ .extractUsername()    │                      │
   │                   │ → "john_doe"          │                      │
   │                   │                       │                      │
   │                   │ loadUserByUsername()  │                      │
   │                   ├──────────────────────>│                      │
   │                   │                       │                      │
   │                   │                       │ findByUsername()     │
   │                   │                       ├─────────────────────>│
   │                   │                       │                      │
   │                   │                       │ UserApp + Role       │
   │                   │                       │<─────────────────────┤
   │                   │                       │                      │
   │                   │                       │ getUserPermissions() │
   │                   │                       │ → Load from          │
   │                   │                       │   role_permissions   │
   │                   │                       │   + user_permissions │
   │                   │                       │                      │
   │                   │ UserDetails           │                      │
   │                   │ (authorities set)     │                      │
   │                   │<──────────────────────┤                      │
   │                   │                       │                      │
   │                   │ Create UsernamePasswordAuthenticationToken   │
   │                   │ Set in SecurityContext│                      │
   │                   │                       │                      │
   │              [2] OAuth2ResourceServerFilter                      │
   │                   │ ConditionalBearerTokenResolver               │
   │                   │ → Already authenticated                      │
   │                   │ → return null (skip)  │                      │
   │                   │                       │                      │
   │              [3] Request proceeds to controller                  │
   │                   │                       │                      │
   │ 200 OK            │                       │                      │
   │ [products]        │                       │                      │
   │<──────────────────┤                       │                      │
```

---

## Component Details

### 1. SecurityConfig.java

**Location**: `src/main/java/org/tricol/supplierchain/config/SecurityConfig.java`

**Key Configurations**:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    // Filter order:
    // 1. JwtAuthenticationFilter (custom, for local JWT)
    // 2. OAuth2ResourceServerFilter (Spring, for Keycloak JWT)
    
    http
        .csrf(disable)
        .sessionManagement(STATELESS)
        .authorizeHttpRequests(...)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2ResourceServer(oauth -> oauth
            .bearerTokenResolver(conditionalBearerTokenResolver)
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );
}
```

**JWT Authentication Converter**:

```java
@Bean
public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    };
}

private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    // ❌ ISSUE: Looks in resource_access.supplier-chain-api.roles
    // ✓ SHOULD: Look in realm_access.roles
    
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    Map<String, Object> resource = resourceAccess.get("supplier-chain-api");
    Collection<String> roles = resource.get("roles");
    
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toSet());
}
```

**URL-Based Authorization**:

| Pattern | Access |
|---------|--------|
| `/api/v1/auth/**` | permitAll |
| `/swagger-ui/**`, `/v3/api-docs/**` | permitAll |
| `/actuator/health` | permitAll |
| `/**` | authenticated |

---

### 2. JwtAuthenticationFilter.java

**Location**: `src/main/java/org/tricol/supplierchain/security/JwtAuthenticationFilter.java`

**Purpose**: Handles local JWT tokens (signed with application secret)

**Flow**:

1. Extract token from `Authorization: Bearer <token>` header
2. Check if already authenticated → skip
3. Try to parse token with local secret (isLocalToken)
4. If successful → local JWT:
   - Extract username
   - Load UserDetails from database
   - Validate token
   - Set authentication in SecurityContext
5. If fails → not a local JWT, let OAuth2 filter handle it

**Key Method**:
```java
private boolean isLocalToken(String token) {
    try {
        jwtService.extractUsername(token);  // Uses app secret
        return true;
    } catch (Exception e) {
        return false;  // Not signed with app secret
    }
}
```

---

### 3. ConditionalBearerTokenResolver.java

**Location**: `src/main/java/org/tricol/supplierchain/security/ConditionalBearerTokenResolver.java`

**Purpose**: Prevents OAuth2 filter from processing if already authenticated by local JWT filter

```java
@Override
public String resolve(HttpServletRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication() != null 
            && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
        return null;  // Skip OAuth2 processing
    }
    return defaultResolver.resolve(request);  // Extract Bearer token
}
```

---

### 4. CurrentUserService.java

**Location**: `src/main/java/org/tricol/supplierchain/service/CurrentUserService.java`

**Purpose**: 
- Retrieve current authenticated user
- Auto-create Keycloak users on first login

**Flow**:

```java
public UserApp getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth.getPrincipal() instanceof Jwt jwt) {
        // Keycloak user
        String keycloakUserId = jwt.getSubject();
        return userRepository.findByKeycloakUserId(keycloakUserId)
            .orElseGet(() -> createKeycloakUser(jwt));
    }
    
    // Local user
    String username = auth.getName();
    return userRepository.findByUsername(username).orElse(null);
}
```

**Role Extraction (CURRENT - BROKEN)**:

```java
private Role extractRoleFromJwt(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    Map<String, Object> resource = resourceAccess.get("supplier-chain-api");
    Collection<String> roles = resource.get("roles");
    
    String roleName = roles.iterator().next();
    return roleRepository.findByName(roleName).orElse(null);
}
```

**Issue**: Token structure is:
```json
{
  "realm_access": {
    "roles": ["ROLE_CHEF_ATELIER", "offline_access", ...]
  },
  "resource_access": {
    "account": {...}  // No "supplier-chain-api"
  }
}
```

**Role Extraction (FIXED)**:

```java
private Role extractRoleFromJwt(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    Collection<String> roles = realmAccess.get("roles");
    
    return roles.stream()
        .filter(role -> role.startsWith("ROLE_"))
        .findFirst()
        .flatMap(roleRepository::findByName)
        .orElse(null);
}
```

---

### 5. PermissionAspect.java

**Location**: `src/main/java/org/tricol/supplierchain/security/PermissionAspect.java`

**Purpose**: Intercepts methods annotated with `@RequirePermission` and enforces permission checks

**Flow**:

```
@Around("@annotation(RequirePermission)")
    ↓
Extract required permission from annotation
    ↓
Get current user via CurrentUserService
    ↓
Check permission via PermissionService.hasPermission()
    ↓
If denied → throw InsufficientPermissionsException (403)
    ↓
If granted → proceed to method
```

**Code**:
```java
@Around("@annotation(org.tricol.supplierchain.security.RequirePermission)")
public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
    RequirePermission annotation = method.getAnnotation(RequirePermission.class);
    String requiredPermission = annotation.value();
    
    UserApp user = currentUserService.getCurrentUser();
    
    if (!permissionService.hasPermission(user, requiredPermission)) {
        throw new InsufficientPermissionsException("Access denied: missing required permissions");
    }
    
    return joinPoint.proceed();
}
```

---

### 6. PermissionServiceImpl.java

**Location**: `src/main/java/org/tricol/supplierchain/service/PermissionServiceImpl.java`

**Purpose**: Calculate effective permissions for a user

**Permission Resolution Logic**:

```
1. Start with empty permission set
    ↓
2. If user has a role:
   Load all permissions from role_permissions table
   Add to permission set
    ↓
3. Load user-specific overrides from user_permissions table
   For each override:
   - If granted=true → ADD permission
   - If granted=false → REMOVE permission
    ↓
4. Return final permission set
```

**Code**:
```java
public Set<String> getUserPermissions(UserApp user) {
    Set<String> permissions = new HashSet<>();
    
    // Role-based permissions
    if (user.getRole() != null) {
        List<RolePermission> rolePermissions = 
            rolePermissionRepository.findByRoleWithPermissions(user.getRole());
        
        permissions = rolePermissions.stream()
            .map(rp -> rp.getPermission().getName())
            .collect(Collectors.toSet());
    }
    
    // User-specific overrides
    List<UserPermission> userPermissions = 
        userPermissionRepository.findByUserWithPermissions(user);
    
    for (UserPermission up : userPermissions) {
        if (Boolean.TRUE.equals(up.getGranted())) {
            permissions.add(up.getPermission().getName());
        } else {
            permissions.remove(up.getPermission().getName());
        }
    }
    
    return permissions;
}
```

**Why 403 Occurred**:
```
user.role = null
    ↓
No role permissions loaded
    ↓
No user-specific permissions
    ↓
permissions = {}
    ↓
"PRODUIT_READ" ∉ {} → FALSE
    ↓
throw InsufficientPermissionsException
```

---

### 7. JwtService.java

**Location**: `src/main/java/org/tricol/supplierchain/security/JwtService.java`

**Purpose**: Generate and validate local JWT tokens

**Configuration**:
- Secret: `security.jwt.secret` (HMAC-SHA)
- Expiration: `security.jwt.access-token-expiration` (24 hours)

**Key Methods**:
- `generateAccessToken(UserDetails)` - Create JWT for local users
- `extractUsername(String token)` - Parse token and get subject
- `isTokenValid(String token, UserDetails)` - Verify signature and expiration

---

### 8. CustomUserDetailsService.java

**Location**: `src/main/java/org/tricol/supplierchain/security/CustomUserDetailsService.java`

**Purpose**: Load user details for local JWT authentication

**Flow**:
```
loadUserByUsername(username)
    ↓
Query database: findByUsername()
    ↓
Check if user.enabled = true
    ↓
Load permissions via PermissionService
    ↓
Build authorities:
  - User permissions (e.g., "PRODUIT_READ")
  - Role authority (e.g., "ROLE_CHEF_ATELIER")
    ↓
Return UserDetails with authorities
```

**Code**:
```java
private Collection<? extends GrantedAuthority> getAuthorities(UserApp user) {
    Set<String> permissions = permissionService.getUserPermissions(user);
    Set<GrantedAuthority> authorities = permissions.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
    
    if (user.getRole() != null) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
    
    return authorities;
}
```

---

## Configuration Files

### application.properties

**Location**: `src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/all_tricol
spring.datasource.username=gaxown
spring.datasource.password=20JvAt02

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Local JWT Configuration
security.jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
security.jwt.access-token-expiration=86400000
security.jwt.refresh-token-expiration=604800000

# Keycloak OAuth2 Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://127.0.0.1:8080/realms/Tricol
```

**Key Points**:
- `issuer-uri`: Spring Security fetches JWKS from `{issuer-uri}/.well-known/openid-configuration`
- JWT signature validation uses Keycloak's public key
- Issuer claim in JWT must match this URI

---

## Current Issue Analysis

### Problem Summary

**Symptom**: Keycloak user created with `role_id = NULL` → 403 Forbidden on all endpoints

**Root Cause**: Role extraction looks in wrong JWT claim path

### JWT Token Structure (Actual)

```json
{
  "sub": "929c81af-15ad-4268-ba79-f5889dff3e69",
  "preferred_username": "testoo",
  "email": "testoo@test.com",
  "realm_access": {
    "roles": [
      "offline_access",
      "ROLE_CHEF_ATELIER",  ← Role is HERE
      "uma_authorization",
      "default-roles-tricol"
    ]
  },
  "resource_access": {
    "account": {
      "roles": ["manage-account", "manage-account-links", "view-profile"]
    }
    // ❌ No "supplier-chain-api" key
  }
}
```

### Code Expectations vs Reality

| Component | Expected Path | Actual Path | Result |
|-----------|--------------|-------------|--------|
| SecurityConfig.extractAuthorities() | `resource_access.supplier-chain-api.roles` | `realm_access.roles` | ❌ Returns empty set |
| CurrentUserService.extractRoleFromJwt() | `resource_access.supplier-chain-api.roles` | `realm_access.roles` | ❌ Returns null |

### Impact Chain

```
extractRoleFromJwt() returns null
    ↓
UserApp created with role = null
    ↓
Saved to database with role_id = NULL
    ↓
PermissionService.getUserPermissions(user)
    ↓
user.getRole() == null → No role permissions loaded
    ↓
permissions = {} (empty set)
    ↓
hasPermission(user, "PRODUIT_READ") → false
    ↓
InsufficientPermissionsException thrown
    ↓
403 Forbidden response
```

### Database State After Failed Login

**users table**:
| id | username | email | keycloak_user_id | role_id | enabled |
|----|----------|-------|------------------|---------|---------|
| 1 | testoo | testoo@test.com | 929c81af-15ad-4268-ba79-f5889dff3e69 | NULL | true |

**Expected**:
| id | username | email | keycloak_user_id | role_id | enabled |
|----|----------|-------|------------------|---------|---------|
| 1 | testoo | testoo@test.com | 929c81af-15ad-4268-ba79-f5889dff3e69 | 2 | true |

Where `role_id = 2` references:

**roles table**:
| id | name | description |
|----|------|-------------|
| 2 | ROLE_CHEF_ATELIER | Chef Atelier Role |

---

## Solution

### Fix 1: Update CurrentUserService.extractRoleFromJwt()

**File**: `src/main/java/org/tricol/supplierchain/service/CurrentUserService.java`

**Change**:
```java
@SuppressWarnings("unchecked")
private Role extractRoleFromJwt(Jwt jwt) {
    try {
        // ✓ FIXED: Extract from realm_access instead of resource_access
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles != null) {
                return roles.stream()
                        .filter(role -> role.startsWith("ROLE_"))
                        .findFirst()
                        .flatMap(roleRepository::findByName)
                        .orElse(null);
            }
        }
    } catch (Exception e) {
        log.error("Failed to extract role from JWT: {}", e.getMessage(), e);
    }
    return null;
}
```

### Fix 2: Update SecurityConfig.extractAuthorities() (Optional)

**File**: `src/main/java/org/tricol/supplierchain/config/SecurityConfig.java`

**Current**:
```java
private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    // ... extracts from resource_access.supplier-chain-api.roles
}
```

**Option A - Use realm_access (Recommended)**:
```java
private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess != null) {
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles != null) {
            return roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }
    return Collections.emptySet();
}
```

**Option B - Configure Keycloak Client Scope**:
Add a client scope mapper in Keycloak to include roles in `resource_access.supplier-chain-api.roles`

---

## Testing Checklist

### Before Fix
- [x] Login with Keycloak user
- [x] User created in database with `role_id = NULL`
- [x] Access `/api/v1/produits` → 403 Forbidden
- [x] Log shows: `"Auto-created Keycloak user: testoo with role: none"`

### After Fix
- [ ] Delete test user from database
- [ ] Login with Keycloak user again
- [ ] Verify user created with correct `role_id`
- [ ] Log shows: `"Auto-created Keycloak user: testoo with role: ROLE_CHEF_ATELIER"`
- [ ] Access `/api/v1/produits` → 200 OK
- [ ] Verify permissions loaded correctly

### SQL Verification
```sql
-- Check user was created with role
SELECT u.id, u.username, u.email, u.role_id, r.name as role_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
WHERE u.keycloak_user_id = '929c81af-15ad-4268-ba79-f5889dff3e69';

-- Check role permissions
SELECT r.name as role_name, p.name as permission_name
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ROLE_CHEF_ATELIER';
```

---

## File Reference Map

### Security Configuration
```
src/main/java/org/tricol/supplierchain/
├── config/
│   └── SecurityConfig.java ..................... Main security configuration
├── security/
│   ├── JwtAuthenticationFilter.java ............ Local JWT filter
│   ├── JwtAuthenticationEntryPoint.java ........ 401 error handler
│   ├── ConditionalBearerTokenResolver.java ..... Skip OAuth2 if authenticated
│   ├── JwtService.java ......................... Local JWT generation/validation
│   ├── CustomUserDetailsService.java ........... Load local users
│   ├── PermissionAspect.java ................... AOP permission enforcement
│   └── RequirePermission.java .................. Permission annotation
└── service/
    ├── CurrentUserService.java ................. Get current user + auto-create
    └── PermissionServiceImpl.java .............. Permission resolution logic
```

### Configuration Files
```
src/main/resources/
└── application.properties ...................... JWT + Keycloak config
```

### Controllers (Examples)
```
src/main/java/org/tricol/supplierchain/controller/
├── ProduitController.java ...................... @RequirePermission("PRODUIT_READ")
├── AuthController.java ......................... Local login/register
└── UserAdminController.java .................... User management
```

### Database Entities
```
src/main/java/org/tricol/supplierchain/entity/
├── UserApp.java ................................ User entity (role_id FK)
├── Role.java ................................... Role entity
├── Permission.java ............................. Permission entity
├── RolePermission.java ......................... Role → Permission mapping
└── UserPermission.java ......................... User-specific overrides
```

---

## Keycloak Configuration Requirements

### Realm Settings
- **Realm Name**: `Tricol`
- **Issuer**: `http://127.0.0.1:8080/realms/Tricol`

### Client Configuration
- **Client ID**: `tricol_oauth` (from JWT `azp` claim)
- **Access Type**: Public or Confidential
- **Valid Redirect URIs**: Configure based on frontend
- **Web Origins**: `*` (from JWT `allowed-origins`)

### Roles
Create realm roles with `ROLE_` prefix:
- `ROLE_CHEF_ATELIER`
- `ROLE_ADMIN`
- `ROLE_USER`
- etc.

### User Assignment
Assign realm roles to users in Keycloak admin console

### Token Claims
Ensure `realm_access.roles` includes the role names that match your database `roles.name` column

---

## Troubleshooting Guide

### Issue: 403 Forbidden after Keycloak login

**Check**:
1. User exists in database: `SELECT * FROM users WHERE keycloak_user_id = '<sub-claim>';`
2. User has role: `SELECT role_id FROM users WHERE id = <user-id>;`
3. Role has permissions: `SELECT * FROM role_permissions WHERE role_id = <role-id>;`
4. JWT contains role: Decode token at jwt.io and check `realm_access.roles`

### Issue: User created with role_id = NULL

**Check**:
1. JWT token structure (decode at jwt.io)
2. Role exists in database: `SELECT * FROM roles WHERE name = 'ROLE_CHEF_ATELIER';`
3. extractRoleFromJwt() logs: Look for extraction errors
4. Role name matches exactly (case-sensitive)

### Issue: Local JWT not working

**Check**:
1. Token signed with correct secret
2. JwtAuthenticationFilter.isLocalToken() returns true
3. User exists in database with matching username
4. User.enabled = true

### Issue: OAuth2 validation fails

**Check**:
1. Keycloak is running and accessible
2. Issuer URI matches: `http://127.0.0.1:8080/realms/Tricol`
3. Token not expired
4. JWKS endpoint accessible: `http://127.0.0.1:8080/realms/Tricol/.well-known/openid-configuration`

---

## Performance Considerations

### Database Queries per Request

**Keycloak User (First Login)**:
1. `SELECT ... FROM users WHERE keycloak_user_id = ?` (not found)
2. `SELECT ... FROM roles WHERE name = ?` (role lookup)
3. `INSERT INTO users ...` (create user)
4. `SELECT ... FROM users WHERE keycloak_user_id = ?` (reload)
5. `SELECT ... FROM role_permissions WHERE role_id = ?` (permissions)
6. `SELECT ... FROM user_permissions WHERE user_id = ?` (overrides)

**Keycloak User (Subsequent Requests)**:
1. `SELECT ... FROM users WHERE keycloak_user_id = ?`
2. `SELECT ... FROM role_permissions WHERE role_id = ?`
3. `SELECT ... FROM user_permissions WHERE user_id = ?`

**Optimization Suggestions**:
- Add `@Cacheable` to `PermissionService.getUserPermissions()`
- Use `@EntityGraph` to fetch role + permissions in single query
- Consider Redis cache for permission sets

---

## Security Best Practices

### ✓ Implemented
- Stateless sessions (no server-side session storage)
- JWT signature validation (both local and Keycloak)
- Token expiration checks
- CSRF disabled (appropriate for stateless API)
- Password encryption (BCrypt with strength 12)
- Separate authentication and authorization layers

### ⚠️ Recommendations
- Rotate JWT secret regularly
- Use environment variables for secrets (not hardcoded defaults)
- Implement token revocation for local JWTs
- Add rate limiting on auth endpoints
- Log security events (login attempts, permission denials)
- Use HTTPS in production
- Validate token audience (`aud` claim)
- Implement refresh token rotation

---

## Conclusion

This application implements a sophisticated dual authentication system supporting both local JWT and Keycloak OAuth2. The current issue stems from a mismatch between the expected JWT claim structure and the actual Keycloak token format. By updating the role extraction logic to read from `realm_access.roles` instead of `resource_access.supplier-chain-api.roles`, Keycloak users will be properly created with their assigned roles, enabling correct permission-based authorization.
