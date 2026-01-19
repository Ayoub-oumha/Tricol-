# 🔐 Keycloak Direct API - Sans Controller/Service

## 🎯 Objectif

Appeler **directement** les endpoints Keycloak sans passer par Spring Boot (pas de Controller, pas de Service).

---

## 📊 Comparaison

| Avec Controller/Service | Direct Keycloak |
|------------------------|-----------------|
| `POST /api/v1/auth/keycloak/login` | `POST http://localhost:8080/realms/Tricol/protocol/openid-connect/token` |
| `POST /api/v1/auth/keycloak/register` | `POST http://localhost:8080/admin/realms/Tricol/users` |
| Code Java nécessaire | Aucun code backend |

---

## 🔑 LOGIN - Appel Direct

### URL
```
POST http://localhost:8080/realms/Tricol/protocol/openid-connect/token
```

### Headers
```
Content-Type: application/x-www-form-urlencoded
```

### Body (form-urlencoded)
```
grant_type=password
client_id=supplier-chain-api
username=votre_username
password=votre_password
```

### Exemple avec cURL
```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=supplier-chain-api" \
  -d "username=testuser" \
  -d "password=password123"
```

### Exemple avec Postman
1. Méthode: **POST**
2. URL: `http://localhost:8080/realms/Tricol/protocol/openid-connect/token`
3. Body → **x-www-form-urlencoded**
4. Ajoutez les paramètres:

| Key | Value |
|-----|-------|
| grant_type | password |
| client_id | supplier-chain-api |
| username | testuser |
| password | password123 |

### Réponse (JSON)
```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
    "expires_in": 300,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "abc123...",
    "scope": "profile email"
}
```

---

## 📝 REGISTER - Appel Direct

### ⚠️ Important
Pour créer un utilisateur directement via l'API Keycloak, vous avez besoin d'un **token admin**.

### Étape 1 : Obtenir un Token Admin

```
POST http://localhost:8080/realms/master/protocol/openid-connect/token
```

Body (form-urlencoded):
```
grant_type=password
client_id=admin-cli
username=admin
password=admin
```

### Étape 2 : Créer l'utilisateur

```
POST http://localhost:8080/admin/realms/Tricol/users
```

Headers:
```
Content-Type: application/json
Authorization: Bearer <admin_token>
```

Body (JSON):
```json
{
    "username": "newuser",
    "email": "newuser@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "emailVerified": true,
    "credentials": [
        {
            "type": "password",
            "value": "password123",
            "temporary": false
        }
    ]
}
```

### Exemple cURL complet
```bash
# 1. Obtenir token admin
ADMIN_TOKEN=$(curl -s -X POST "http://localhost:8080/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" | jq -r '.access_token')

# 2. Créer utilisateur
curl -X POST "http://localhost:8080/admin/realms/Tricol/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "credentials": [{"type": "password", "value": "password123", "temporary": false}]
  }'
```

---

## 🔄 REFRESH TOKEN - Appel Direct

### URL
```
POST http://localhost:8080/realms/Tricol/protocol/openid-connect/token
```

### Body (form-urlencoded)
```
grant_type=refresh_token
client_id=supplier-chain-api
refresh_token=<votre_refresh_token>
```

### Exemple cURL
```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=supplier-chain-api" \
  -d "refresh_token=eyJhbGciOiJIUzI1NiIs..."
```

---

## 🚪 LOGOUT - Appel Direct

### URL
```
POST http://localhost:8080/realms/Tricol/protocol/openid-connect/logout
```

### Body (form-urlencoded)
```
client_id=supplier-chain-api
refresh_token=<votre_refresh_token>
```

### Exemple cURL
```bash
curl -X POST "http://localhost:8080/realms/Tricol/protocol/openid-connect/logout" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=supplier-chain-api" \
  -d "refresh_token=eyJhbGciOiJIUzI1NiIs..."
```

---

## 📱 REGISTER via Page Keycloak (Le Plus Simple!)

Si vous voulez que les utilisateurs s'inscrivent **sans code backend du tout**, activez la page d'inscription Keycloak :

### Configuration Keycloak Admin Console

1. Connectez-vous à `http://localhost:8080/admin`
2. Sélectionnez le realm **Tricol**
3. Allez dans **Realm Settings** → **Login**
4. Activez **User registration** ✅

### URL d'inscription
```
http://localhost:8080/realms/Tricol/protocol/openid-connect/registrations?client_id=supplier-chain-api&response_type=code&redirect_uri=http://localhost:3000
```

L'utilisateur verra une page Keycloak pour s'inscrire !

---

## 📋 Résumé des Endpoints Directs Keycloak

| Action | Méthode | URL |
|--------|---------|-----|
| **Login** | POST | `http://localhost:8080/realms/Tricol/protocol/openid-connect/token` |
| **Register** (API) | POST | `http://localhost:8080/admin/realms/Tricol/users` |
| **Register** (Page) | GET | `http://localhost:8080/realms/Tricol/protocol/openid-connect/registrations` |
| **Refresh Token** | POST | `http://localhost:8080/realms/Tricol/protocol/openid-connect/token` |
| **Logout** | POST | `http://localhost:8080/realms/Tricol/protocol/openid-connect/logout` |
| **User Info** | GET | `http://localhost:8080/realms/Tricol/protocol/openid-connect/userinfo` |

---

## 💻 Exemple Frontend JavaScript (Sans Backend)

```html
<!DOCTYPE html>
<html>
<head>
    <title>Keycloak Direct</title>
</head>
<body>
    <h1>Login Direct Keycloak</h1>
    
    <input type="text" id="username" placeholder="Username">
    <input type="password" id="password" placeholder="Password">
    <button onclick="login()">Login</button>
    
    <pre id="result"></pre>

    <script>
        async function login() {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            // Appel DIRECT à Keycloak (pas de backend!)
            const response = await fetch('http://localhost:8080/realms/Tricol/protocol/openid-connect/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'grant_type': 'password',
                    'client_id': 'supplier-chain-api',
                    'username': username,
                    'password': password
                })
            });
            
            const data = await response.json();
            document.getElementById('result').textContent = JSON.stringify(data, null, 2);
            
            // Stocker le token
            if (data.access_token) {
                localStorage.setItem('access_token', data.access_token);
                localStorage.setItem('refresh_token', data.refresh_token);
                alert('Login réussi!');
            }
        }
    </script>
</body>
</html>
```

---

## ⚠️ Configuration Keycloak Requise

Pour que les appels directs fonctionnent, configurez votre client Keycloak :

1. **Client** → `supplier-chain-api`
2. **Access Type** : `public` (ou `confidential` avec secret)
3. **Direct Access Grants Enabled** : ✅ **ON** (pour grant_type=password)
4. **Web Origins** : `*` ou `http://localhost:3000` (pour éviter CORS)

---

## 🎓 Pour vos Apprenants

| Ce qu'on évite | Ce qu'on utilise |
|----------------|------------------|
| `KeycloakAuthController.java` | Appels HTTP directs |
| `KeycloakServiceImpl.java` | Endpoints Keycloak natifs |
| Code Java | Postman / cURL / JavaScript |

**Conclusion** : Keycloak fournit tous les endpoints nécessaires. Le Controller/Service Spring Boot est optionnel et sert principalement à ajouter de la logique métier personnalisée.

