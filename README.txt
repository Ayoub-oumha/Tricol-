End-to-End Keycloak Authentication & Authorization Workflow
Based on your codebase, here's the complete flow:

1. Initial Request Handling
Entry Point: SecurityConfig.java

Two filters intercept requests in this order:

JwtAuthenticationFilter (runs BEFORE UsernamePasswordAuthenticationFilter)

Checks for Authorization: Bearer <token> header

If token is a local JWT (signed with your secret), validates it using JwtService

Loads user from database via CustomUserDetailsService

Sets authentication in SecurityContext

OAuth2 Resource Server (configured in SecurityConfig)

If JwtAuthenticationFilter didn't authenticate (Keycloak token), this handles it

Uses ConditionalBearerTokenResolver to skip if already authenticated

2. Token Validation
For Keycloak Tokens:

Configuration Source: application.properties

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://127.0.0.1:8080/realms/Tricol

Copy
properties
Validation Process:

Spring Security's OAuth2 Resource Server automatically:

Fetches JWKS (public keys) from http://127.0.0.1:8080/realms/Tricol/.well-known/openid-configuration

Verifies JWT signature using Keycloak's public key

Validates iss claim matches the issuer-uri

Checks token expiration (exp claim)

For Local Tokens:

Configuration Source: application.properties

security.jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
security.jwt.access-token-expiration=86400000

Copy
properties
Validation in JwtService:

Uses HMAC-SHA with the secret key

Validates signature and expiration

3. Identity & Role Extraction
For Keycloak Tokens:

SecurityConfig.jwtAuthenticationConverter()

private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    // Extracts from: resource_access.supplier-chain-api.roles
    // Prefixes with "ROLE_" → e.g., "ROLE_CHEF_ATELIER"
}

Copy
ISSUE IDENTIFIED: Your token has roles in realm_access.roles, NOT resource_access.supplier-chain-api.roles. This is why Spring Security authorities work but database role extraction fails.

Database User Creation:

CurrentUserService.getCurrentUser()

Checks if Keycloak user exists in database by keycloakUserId (JWT sub claim)

If not found, calls createKeycloakUser(jwt)

CurrentUserService.extractRoleFromJwt()

Currently extracts from resource_access.supplier-chain-api.roles (returns null for your token)

Should extract from realm_access.roles instead

4. Method/URL Security
Two-Level Authorization:

Level 1: URL-Based (SecurityConfig)

.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/**").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers("/actuator/health").permitAll()
    .anyRequest().authenticated()  // Only requires authentication, not specific roles
)

Copy
java
Level 2: Permission-Based (Method Level)

ProduitController example:

@GetMapping
@RequirePermission("PRODUIT_READ")  // Custom annotation
public ResponseEntity<List<ProduitResponseDTO>> getAllProduits() {...}

Copy
java
Enforcement via PermissionAspect:

Intercepts methods annotated with @RequirePermission

Gets current user via CurrentUserService

Checks permission via PermissionServiceImpl

PermissionServiceImpl.getUserPermissions():

Loads permissions from role_permissions table based on user's role

Applies user-specific overrides from user_permissions table

Returns final permission set

Why you got 403:

User was created with role_id = null (extraction failed)

No role → no role permissions → access denied

5. Configuration Files
Security Configuration:

SecurityConfig.java - Main security setup

application.properties - JWT & Keycloak config

Authentication Components:

JwtAuthenticationFilter.java

JwtAuthenticationEntryPoint.java

ConditionalBearerTokenResolver.java

JwtService.java

CustomUserDetailsService.java

Authorization Components:

PermissionAspect.java

RequirePermission.java

PermissionServiceImpl.java

CurrentUserService.java

Database Entities:

UserApp.java

Role.java

Permission.java, RolePermission.java, UserPermission.java

Root Cause of Your Issue
Your Keycloak token structure:

{
  "realm_access": {"roles": ["ROLE_CHEF_ATELIER", ...]},
  "resource_access": {"account": {...}}  // No "supplier-chain-api"
}

Copy
json
Both SecurityConfig and CurrentUserService look for resource_access.supplier-chain-api.roles, which doesn't exist. The fix I provided changes CurrentUserService to extract from realm_access.roles instead.


