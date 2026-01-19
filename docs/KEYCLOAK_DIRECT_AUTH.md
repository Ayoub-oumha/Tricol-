# 🔐 Authentification Keycloak : Deux Approches

## 📊 Comparaison des Approches

| Aspect | Approche Directe (Sans Controller) | Approche Custom (Avec Controller/Service) |
|--------|-----------------------------------|------------------------------------------|
| **Code à écrire** | ❌ Aucun | ✅ Controller + Service |
| **Pages Login/Register** | Fournies par Keycloak | Custom (React, Angular, etc.) |
| **Personnalisation UI** | Via thèmes Keycloak | Totale liberté |
| **Sécurité** | ✅ Gérée par Keycloak | ⚠️ À implémenter |
| **Complexité** | Simple | Plus complexe |
| **Cas d'usage** | Applications standards | Besoins spécifiques |

---

## 🚀 Approche 1 : Login/Register DIRECTEMENT via Keycloak (Sans Code Backend)

### Comment ça marche ?

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────┐
│   Browser   │────▶│  Keycloak Pages │────▶│   Votre     │
│             │     │  (Login/Register)│     │   App       │
│             │◀────│                  │◀────│             │
│             │token│                  │     │             │
└─────────────┘     └─────────────────┘     └─────────────┘
```

### Configuration Spring Security (Authorization Code Flow)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            // 👇 Cette ligne active la redirection vers Keycloak
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        
        return http.build();
    }
}
```

### Configuration application.properties

```properties
# Keycloak OAuth2 Configuration
spring.security.oauth2.client.registration.keycloak.client-id=supplier-chain-api
spring.security.oauth2.client.registration.keycloak.client-secret=your-secret
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

# Keycloak Provider Configuration
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/realms/Tricol
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
```

### Dépendance Maven

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### URLs Keycloak Automatiques

| Action | URL Keycloak |
|--------|--------------|
| **Login** | `http://localhost:8080/realms/Tricol/protocol/openid-connect/auth` |
| **Register** | `http://localhost:8080/realms/Tricol/protocol/openid-connect/registrations` |
| **Logout** | `http://localhost:8080/realms/Tricol/protocol/openid-connect/logout` |
| **Account** | `http://localhost:8080/realms/Tricol/account` |

### Activer l'inscription dans Keycloak

1. Allez dans **Keycloak Admin Console**
2. Sélectionnez votre **Realm** (Tricol)
3. **Realm Settings** → **Login** tab
4. Activez **User registration** ✅

![Enable Registration](https://www.keycloak.org/docs/latest/server_admin/images/login-tab.png)

---

## 🛠️ Approche 2 : Login/Register via Controller/Service (Votre Code Actuel)

### Quand utiliser cette approche ?

- ✅ Vous voulez une **UI personnalisée** (React, Angular, Vue)
- ✅ Vous avez besoin de **logique métier** lors de l'inscription
- ✅ Vous voulez **stocker des données supplémentaires** en base locale
- ✅ Vous avez une **application mobile** (pas de redirection web)

### Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│  Spring Boot │────▶│  Keycloak   │
│   Custom UI │     │  Controller  │     │  Admin API  │
│             │◀────│  + Service   │◀────│             │
└─────────────┘     └─────────────┘     └─────────────┘
```

### C'est ce que fait votre `KeycloakServiceImpl.java` :

```java
// REGISTER : Appelle l'API Admin Keycloak
POST /admin/realms/{realm}/users

// LOGIN : Appelle le endpoint token
POST /realms/{realm}/protocol/openid-connect/token
```

---

## 🎯 Exemple Pratique : Frontend avec Approche Directe

### HTML Simple (Sans Backend)

```html
<!DOCTYPE html>
<html>
<head>
    <title>Login avec Keycloak</title>
    <script src="https://cdn.jsdelivr.net/npm/keycloak-js@22.0.1/dist/keycloak.min.js"></script>
</head>
<body>
    <h1>Mon Application</h1>
    <button id="loginBtn">Se Connecter</button>
    <button id="logoutBtn" style="display:none">Se Déconnecter</button>
    <button id="registerBtn">S'inscrire</button>
    <div id="userInfo"></div>

    <script>
        // Configuration Keycloak
        const keycloak = new Keycloak({
            url: 'http://localhost:8080',
            realm: 'Tricol',
            clientId: 'supplier-chain-api'
        });

        // Initialisation
        keycloak.init({ onLoad: 'check-sso' }).then(authenticated => {
            if (authenticated) {
                document.getElementById('loginBtn').style.display = 'none';
                document.getElementById('registerBtn').style.display = 'none';
                document.getElementById('logoutBtn').style.display = 'block';
                document.getElementById('userInfo').innerHTML = 
                    `Bienvenue ${keycloak.tokenParsed.preferred_username}!`;
            }
        });

        // 👇 LOGIN - Redirection vers page Keycloak
        document.getElementById('loginBtn').onclick = () => {
            keycloak.login();
        };

        // 👇 REGISTER - Redirection vers page inscription Keycloak
        document.getElementById('registerBtn').onclick = () => {
            keycloak.register();
        };

        // 👇 LOGOUT
        document.getElementById('logoutBtn').onclick = () => {
            keycloak.logout();
        };
    </script>
</body>
</html>
```

### Résultat

- Cliquer sur **"Se Connecter"** → Redirige vers la page login Keycloak
- Cliquer sur **"S'inscrire"** → Redirige vers la page register Keycloak
- **Aucun code backend nécessaire !**

---

## 📱 Pour une Application React

```jsx
// npm install keycloak-js @react-keycloak/web

import Keycloak from 'keycloak-js';
import { ReactKeycloakProvider } from '@react-keycloak/web';

const keycloak = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'Tricol',
    clientId: 'supplier-chain-api'
});

function App() {
    return (
        <ReactKeycloakProvider authClient={keycloak}>
            <div>
                <button onClick={() => keycloak.login()}>Login</button>
                <button onClick={() => keycloak.register()}>Register</button>
            </div>
        </ReactKeycloakProvider>
    );
}
```

---

## 🔄 Résumé : Quelle Approche Choisir ?

### Utilisez l'approche DIRECTE (sans controller) si :
- 🎯 Vous voulez aller vite
- 🎯 Les pages Keycloak par défaut vous conviennent
- 🎯 Vous n'avez pas de logique métier spéciale à l'inscription

### Utilisez l'approche avec CONTROLLER/SERVICE si :
- 🎯 Vous voulez une UI totalement personnalisée
- 🎯 Vous devez synchroniser les users avec votre base locale
- 🎯 Vous avez une app mobile native
- 🎯 Vous voulez ajouter de la logique métier (validation, notifications, etc.)

---

## 🎓 Exercice pour les Apprenants

1. **Exercice 1** : Configurez votre realm Keycloak pour activer l'inscription directe
2. **Exercice 2** : Créez une page HTML simple qui utilise `keycloak-js` pour login/register
3. **Exercice 3** : Comparez les deux approches en termes de sécurité

---

## 📚 Ressources

- [Keycloak JavaScript Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_javascript_adapter)
- [Spring Security OAuth2 Login](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html)
- [Authorization Code Flow](https://oauth.net/2/grant-types/authorization-code/)

