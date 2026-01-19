# 📚 Explication du Service Keycloak - Register & Login

## 🎯 Introduction

Ce document explique le fonctionnement du service `KeycloakServiceImpl.java` qui gère l'authentification des utilisateurs avec **Keycloak** (un serveur d'identité open source).

---

## 🏗️ Structure de la Classe

```java
@Slf4j        // Permet d'utiliser les logs (log.info, log.error)
@Service      // Indique à Spring que c'est un service injectable
public class KeycloakServiceImpl implements KeycloakService {
```

### Les Dépendances Injectées

| Variable | Description |
|----------|-------------|
| `restTemplate` | Client HTTP pour faire des requêtes vers Keycloak |
| `objectMapper` | Convertit les objets Java en JSON et vice-versa |

### Les Configurations (depuis `application.properties`)

```java
@Value("${keycloak.auth-server-url:http://localhost:8080}")
private String keycloakServerUrl;  // URL du serveur Keycloak

@Value("${keycloak.realm:Tricol}")
private String realm;              // Le "royaume" Keycloak (espace isolé)

@Value("${keycloak.client-id:supplier-chain-api}")
private String clientId;           // Identifiant de l'application

@Value("${keycloak.client-secret:}")
private String clientSecret;       // Secret de l'application (optionnel)
```

> 💡 **Note**: `@Value` injecte les valeurs depuis le fichier de configuration. La syntaxe `${property:defaultValue}` fournit une valeur par défaut.

---

## 📝 Méthode REGISTER (Inscription)

### Objectif
Créer un nouvel utilisateur dans Keycloak.

### Flux d'exécution

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Application   │────▶│   Keycloak      │────▶│  Base de        │
│   Spring Boot   │     │   Admin API     │     │  données KC     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Code Expliqué Étape par Étape

#### Étape 1 : Obtenir un token administrateur

```java
String adminToken = getAdminAccessToken();
```

> ⚠️ Pour créer un utilisateur, on a besoin des droits admin. On récupère d'abord un token admin.

#### Étape 2 : Construire l'URL de l'API

```java
String usersUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";
// Exemple: http://localhost:8080/admin/realms/Tricol/users
```

#### Étape 3 : Préparer les Headers HTTP

```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);  // On envoie du JSON
headers.setBearerAuth(adminToken);                   // Token d'authentification
```

> 💡 **Bearer Auth** : Le token est envoyé dans le header `Authorization: Bearer <token>`

#### Étape 4 : Construire les données de l'utilisateur

```java
Map<String, Object> userRepresentation = new HashMap<>();
userRepresentation.put("username", request.getUsername());
userRepresentation.put("email", request.getEmail());
userRepresentation.put("firstName", request.getFirstName());
userRepresentation.put("lastName", request.getLastName());
userRepresentation.put("enabled", true);         // Compte actif
userRepresentation.put("emailVerified", true);   // Email vérifié
```

#### Étape 5 : Configurer le mot de passe

```java
Map<String, Object> credentials = new HashMap<>();
credentials.put("type", "password");
credentials.put("value", request.getPassword());
credentials.put("temporary", false);  // false = pas besoin de changer au 1er login
userRepresentation.put("credentials", Collections.singletonList(credentials));
```

#### Étape 6 : Envoyer la requête POST

```java
HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userRepresentation, headers);

ResponseEntity<String> response = restTemplate.exchange(
    usersUrl,           // URL
    HttpMethod.POST,    // Méthode HTTP
    entity,             // Corps + Headers
    String.class        // Type de réponse attendu
);
```

#### Étape 7 : Traiter la réponse

```java
if (response.getStatusCode() == HttpStatus.CREATED) {  // Code 201 = Créé
    // Récupérer l'ID utilisateur depuis le header Location
    String locationHeader = response.getHeaders().getFirst("Location");
    String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    
    return KeycloakRegisterResponse.builder()
        .status("SUCCESS")
        .message("User registered successfully")
        .userId(userId)
        .username(request.getUsername())
        .build();
}
```

### Gestion des Erreurs

```java
catch (HttpClientErrorException e) {
    if (e.getStatusCode() == HttpStatus.CONFLICT) {  // Code 409
        throw new BusinessException("User already exists");
    }
    throw new BusinessException("Failed to register: " + e.getMessage());
}
```

| Code HTTP | Signification |
|-----------|---------------|
| 201 | ✅ Utilisateur créé avec succès |
| 409 | ⚠️ L'utilisateur existe déjà |
| 401 | ❌ Token admin invalide |
| 400 | ❌ Données invalides |

---

## 🔐 Méthode LOGIN (Connexion)

### Objectif
Authentifier un utilisateur et obtenir des tokens JWT.

### Flux d'exécution

```
┌─────────────┐                    ┌─────────────┐
│   Client    │  username/password │   Keycloak  │
│             │ ──────────────────▶│             │
│             │                    │             │
│             │  access_token +    │             │
│             │ ◀────────────────── │             │
│             │  refresh_token     │             │
└─────────────┘                    └─────────────┘
```

### Code Expliqué

#### Étape 1 : URL du endpoint token

```java
String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
// Exemple: http://localhost:8080/realms/Tricol/protocol/openid-connect/token
```

#### Étape 2 : Préparer la requête (format formulaire)

```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // ⚠️ Pas JSON!

MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
body.add("grant_type", "password");           // Type d'authentification
body.add("client_id", clientId);              // ID de l'application
body.add("username", request.getUsername());  // Nom d'utilisateur
body.add("password", request.getPassword());  // Mot de passe
```

> 💡 **grant_type=password** : C'est le "Resource Owner Password Credentials" flow d'OAuth 2.0

#### Étape 3 : Ajouter le client_secret si configuré

```java
if (clientSecret != null && !clientSecret.isEmpty()) {
    body.add("client_secret", clientSecret);
}
```

#### Étape 4 : Envoyer la requête et recevoir les tokens

```java
ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
    tokenUrl,
    HttpMethod.POST,
    entity,
    KeycloakTokenResponse.class  // Désérialisation automatique
);

return response.getBody();  // Contient access_token, refresh_token, etc.
```

### Structure de la Réponse Token

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIs...",    // JWT pour les requêtes API
  "expires_in": 300,                             // Durée de vie (5 min)
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",   // Pour renouveler
  "refresh_expires_in": 1800,                    // Durée refresh (30 min)
  "token_type": "Bearer"
}
```

---

## 🔄 Méthode REFRESH TOKEN

### Objectif
Obtenir un nouveau `access_token` sans redemander le mot de passe.

```java
body.add("grant_type", "refresh_token");  // ⚠️ Différent du login!
body.add("refresh_token", refreshToken);
```

> 💡 **Pourquoi?** L'access_token expire vite (5 min). Le refresh_token permet d'en obtenir un nouveau sans déranger l'utilisateur.

---

## 🚪 Méthode LOGOUT (Déconnexion)

### Objectif
Invalider les tokens de l'utilisateur.

```java
String logoutUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

body.add("client_id", clientId);
body.add("refresh_token", refreshToken);  // Keycloak invalide ce token
```

---

## 🔑 Méthode getAdminAccessToken (Privée)

### Objectif
Obtenir un token admin pour les opérations d'administration (créer users, etc.)

```java
String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";
// ⚠️ Note: On utilise le realm "master" (realm admin de Keycloak)

body.add("client_id", "admin-cli");     // Client admin par défaut
body.add("username", adminUsername);     // Admin Keycloak
body.add("password", adminPassword);
```

---

## 📊 Résumé des Endpoints Keycloak

| Action | Méthode | URL |
|--------|---------|-----|
| Register | POST | `/admin/realms/{realm}/users` |
| Login | POST | `/realms/{realm}/protocol/openid-connect/token` |
| Refresh | POST | `/realms/{realm}/protocol/openid-connect/token` |
| Logout | POST | `/realms/{realm}/protocol/openid-connect/logout` |

---

## 🎓 Concepts Clés à Retenir

### 1. OAuth 2.0 Grant Types
- `password` : Login avec username/password
- `refresh_token` : Renouvellement du token
- `client_credentials` : Authentification machine-to-machine

### 2. Tokens JWT
- **Access Token** : Court durée, utilisé pour chaque requête API
- **Refresh Token** : Long durée, utilisé pour obtenir un nouveau access token

### 3. RestTemplate
- Client HTTP de Spring pour faire des requêtes REST
- `exchange()` : Méthode générique pour tous types de requêtes

### 4. Pattern Builder
```java
KeycloakRegisterResponse.builder()
    .status("SUCCESS")
    .message("...")
    .build();
```

---

## 🧪 Exercices pour les Apprenants

1. **Question** : Pourquoi utilise-t-on `APPLICATION_FORM_URLENCODED` pour le login et `APPLICATION_JSON` pour le register?

2. **Question** : Quelle est la différence entre le realm "master" et le realm "Tricol"?

3. **Exercice** : Ajoutez une méthode pour changer le mot de passe d'un utilisateur.

4. **Exercice** : Implémentez une méthode pour assigner un rôle à un utilisateur après l'inscription.

---

## 📚 Ressources

- [Documentation Keycloak](https://www.keycloak.org/documentation)
- [OAuth 2.0 RFC](https://oauth.net/2/)
- [Spring RestTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)

