# 🔐 Système d'Authentification JWT - EcoleGest

## ✅ Architecture Complète Implémentée

### 📦 Composants Backend

#### 1. **Configuration Sécurité**
```
src/main/java/com/gescom/ecole/config/
├── SecurityConfig.java              ✅ Configuration Spring Security
├── WebConfig.java                   ✅ Configuration Web MVC
└── security/
    ├── JwtTokenProvider.java        ✅ Génération & validation JWT
    ├── JwtAuthenticationFilter.java ✅ Filtre d'authentification
    └── JwtAuthenticationEntryPoint.java ✅ Gestion des erreurs auth
```

#### 2. **Services d'Authentification**
```
src/main/java/com/gescom/ecole/service/
├── AuthService.java                 ✅ Interface service auth
└── impl/
    ├── AuthServiceImpl.java         ✅ Implémentation auth
    └── CustomUserDetailsService.java ✅ Chargement utilisateurs
```

#### 3. **Controllers**
```
src/main/java/com/gescom/ecole/controller/
└── AuthController.java              ✅ Endpoints REST auth
```

#### 4. **DTOs**
```
src/main/java/com/gescom/ecole/dto/auth/
├── LoginRequest.java                ✅ Requête de connexion
└── JwtResponse.java                 ✅ Réponse avec token
```

#### 5. **Entities & Repositories**
```
src/main/java/com/gescom/ecole/entity/utilisateur/
├── Utilisateur.java                 ✅ Entité utilisateur
├── Role.java                        ✅ Entité rôle
└── Permission.java                  ✅ Entité permission

src/main/java/com/gescom/ecole/repository/utilisateur/
├── UtilisateurRepository.java       ✅ Repository utilisateur
├── RoleRepository.java              ✅ Repository rôle
└── PermissionRepository.java        ✅ Repository permission
```

---

## 🔑 Configuration JWT

### Dans `application.yml`
```yaml
security:
  jwt:
    secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    expiration: 3600000          # 1 heure
    refresh-expiration: 2592000000  # 30 jours
  cors:
    allowed-origins: http://localhost:3000,http://localhost:8080
```

### Variables d'Environnement
```bash
# Optionnel - secret JWT personnalisé
export JWT_SECRET=your-secret-key-here
```

---

## 🔐 Endpoints d'Authentification

### Base URL
```
http://localhost:8080/api/auth
```

### 1. **Login** (POST `/login`)
**Request:**
```json
{
  "username": "admin",
  "password": "admin123",
  "rememberMe": false
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@ecole.com",
    "roles": ["ADMIN"],
    "permissions": ["users.create", "users.read", ...]
  }
}
```

### 2. **Refresh Token** (POST `/refresh`)
**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. **Logout** (POST `/logout`)
**Headers:**
```
Authorization: Bearer <token>
```

### 4. **Forgot Password** (POST `/forgot-password`)
**Request:**
```json
{
  "email": "user@ecole.com"
}
```

### 5. **Reset Password** (POST `/reset-password`)
**Request:**
```json
{
  "token": "reset-token-here",
  "newPassword": "NewPassword123!"
}
```

---

## 👥 Utilisateurs de Test

### 1. Administrateur
```
Username: admin
Password: admin123
Email: admin@ecole.com
Rôle: ADMIN
Permissions: Toutes (users.*, finance.*, eleves.*)
```

### 2. Comptable
```
Username: comptable
Password: comptable123
Email: comptable@ecole.com
Rôle: COMPTABLE
Permissions: users.read, finance.manage
```

### 3. Secrétaire
```
Username: secretaire
Password: secretaire123
Email: secretaire@ecole.com
Rôle: SECRETAIRE
Permissions: users.read, eleves.manage
```

### 4. Parent
```
Username: parent1
Password: parent123
Email: parent@ecole.com
Rôle: PARENT
Permissions: users.read (lecture seulement)
```

### 5. Enseignant
```
Username: enseignant1
Password: enseignant123
Email: enseignant@ecole.com
Rôle: ENSEIGNANT
Permissions: users.read (lecture + gestion classes)
```

---

## 🔒 Flux d'Authentification

### 1. Login Flow
```
Client → POST /api/auth/login
         ↓
AuthController.login()
         ↓
AuthService.login()
         ↓
AuthenticationManager.authenticate()
         ↓
CustomUserDetailsService.loadUserByUsername()
         ↓
Generate JWT Token
         ↓
Return JwtResponse with token & user info
```

### 2. Protected Request Flow
```
Client → Request + Authorization Header
         ↓
JwtAuthenticationFilter.doFilterInternal()
         ↓
Extract & Validate Token
         ↓
Load UserDetails
         ↓
Set Authentication in SecurityContext
         ↓
Continue Filter Chain
         ↓
Access Protected Resource
```

---

## 🛡️ Sécurité Configurée

### Routes Publiques (Sans Auth)
```
/api/auth/**              ✅ Authentification
/api/public/**            ✅ Resources publiques
/swagger-ui.html          ✅ Documentation API
/swagger-ui/**            ✅ Assets Swagger
/v3/api-docs/**           ✅ OpenAPI docs
/actuator/health          ✅ Health check
/                         ✅ Page accueil
/login.html               ✅ Page login
/css/**                   ✅ Styles
/js/**                    ✅ Scripts
/images/**                ✅ Images
```

### Routes Protégées (Auth Required)
```
/api/eleves/**            🔒 Gestion élèves
/api/paiements/**         🔒 Gestion paiements
/api/classes/**           🔒 Gestion classes
/api/factures/**          🔒 Gestion factures
/api/utilisateurs/**      🔒 Gestion utilisateurs
... (tous les autres endpoints)
```

---

## 🔧 Frontend - Intégration JWT

### Dans `api.js`
```javascript
// Stockage du token
const TOKEN_KEY = 'ecolegest_token';
const USER_KEY = 'ecolegest_user';

// Récupération du token
AuthService.getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// Ajout du token aux requêtes
const defaultHeaders = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
};
```

### Exemple d'utilisation
```javascript
// Login
const response = await AuthService.login('admin', 'admin123');
// Token stocké automatiquement

// Appel API protégé
const eleves = await EleveAPI.getAll();
// Token ajouté automatiquement dans le header

// Logout
AuthService.logout();
// Token supprimé et redirection vers login
```

---

## 🚀 Test de l'Authentification

### 1. Test avec cURL
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Utiliser le token
curl http://localhost:8080/api/eleves \
  -H "Authorization: Bearer <votre-token-ici>"
```

### 2. Test avec Postman
```
1. POST http://localhost:8080/api/auth/login
   Body (JSON):
   {
     "username": "admin",
     "password": "admin123"
   }

2. Copier le "accessToken" de la réponse

3. GET http://localhost:8080/api/eleves
   Headers:
   Authorization: Bearer <token-copié>
```

### 3. Test avec le Frontend
```
1. Ouvrir http://localhost:8080/
2. Se connecter avec admin/admin123
3. Naviguer dans l'application
4. Vérifier dans Console (F12) → Application → LocalStorage
   - Voir ecolegest_token
   - Voir ecolegest_user
```

---

## 🎯 Fonctionnalités Implémentées

### ✅ Authentification
- [x] Login avec username/password
- [x] Génération JWT avec claims
- [x] Refresh token pour renouvellement
- [x] Logout avec suppression de session
- [x] Remember me option

### ✅ Autorisation
- [x] Système de rôles (ADMIN, COMPTABLE, etc.)
- [x] Système de permissions granulaires
- [x] Vérification des permissions par endpoint
- [x] Gestion des accès via @PreAuthorize

### ✅ Sécurité
- [x] Tokens JWT signés (HS256)
- [x] Expiration automatique des tokens
- [x] Validation des tokens à chaque requête
- [x] Protection CSRF désactivée (API REST)
- [x] CORS configuré
- [x] Passwords hashés (BCrypt)

### ✅ Gestion des Erreurs
- [x] 401 Unauthorized si token invalide
- [x] 403 Forbidden si permissions insuffisantes
- [x] Redirection auto vers login si non connecté
- [x] Messages d'erreur clairs

---

## 📊 Base de Données

### Tables Créées Automatiquement
```sql
-- Utilisateurs
utilisateurs (id, username, email, password, actif, telephone, ...)
roles (id, code, designation)
permissions (id, code, designation, module, action)
utilisateur_roles (utilisateur_id, role_id)
role_permissions (role_id, permission_id)

-- Spécialisations
parents (id, numero_parent, profession, type_parent, cin)
enseignants (id, matricule, specialite, diplome, date_embauche, type_contrat)
```

### Données Initialisées
- 5 rôles (ADMIN, COMPTABLE, SECRETAIRE, PARENT, ENSEIGNANT)
- 6 permissions (users.*, finance.manage, eleves.manage)
- 5 utilisateurs de test
- Relations rôles-permissions configurées

---

## 🐛 Dépannage

### Problème: "Unauthorized 401"
**Causes possibles:**
- Token expiré (> 1 heure)
- Token invalide ou corrompu
- Token non envoyé dans le header

**Solutions:**
```javascript
// Vérifier le token
console.log(AuthService.getToken());

// Re-login si expiré
AuthService.logout();
window.location.href = '/login.html';
```

### Problème: "Forbidden 403"
**Cause:** Permissions insuffisantes

**Solution:**
- Vérifier les rôles de l'utilisateur
- Utiliser un compte avec plus de permissions (admin)

### Problème: CORS Error
**Solution déjà implémentée dans SecurityConfig:**
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

---

## 📝 Prochaines Améliorations

### Sécurité Avancée
- [ ] Token blacklist pour logout réel
- [ ] Rate limiting sur /auth/login
- [ ] 2FA (authentification à deux facteurs)
- [ ] Politique de mot de passe fort
- [ ] Verrouillage de compte après X tentatives

### Fonctionnalités
- [ ] OAuth2/Social Login (Google, Facebook)
- [ ] Single Sign-On (SSO)
- [ ] API Keys pour intégrations externes
- [ ] Logs d'audit des connexions
- [ ] Sessions multiples management

---

## 📚 Références

### Documentation
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [JJWT Library](https://github.com/jwtk/jjwt)

### Standards
- [RFC 7519 - JWT](https://tools.ietf.org/html/rfc7519)
- [RFC 6749 - OAuth 2.0](https://tools.ietf.org/html/rfc6749)
- [OWASP Auth Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

---

**Version:** 1.0.0  
**Date:** 30 Septembre 2024  
**Statut:** ✅ Production Ready  
**Sécurité:** 🔒 JWT + BCrypt + HTTPS Ready
