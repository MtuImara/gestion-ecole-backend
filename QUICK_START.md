# 🚀 Guide de Démarrage Rapide - EcoleGest

## ⚡ Démarrage en 3 Étapes

### 1️⃣ Lancer l'Application
```bash
cd e:/vide/Gescom/gestion-ecole-backend
mvn spring-boot:run
```

**Attendre le message :**
```
Started GestionEcoleApplication in X seconds
```

### 2️⃣ Ouvrir le Navigateur
```
http://localhost:8080/
```

### 3️⃣ Se Connecter
```
Username: admin
Password: admin123
```

---

## ✅ Test Rapide de l'Application

### Test 1: Login ✅
1. Ouvrir `http://localhost:8080/`
2. Entrer `admin` / `admin123`
3. Cliquer "Se connecter"
4. ✅ Redirection vers Dashboard

### Test 2: Dashboard ✅
1. Vérifier les 4 cartes de statistiques
2. Voir la liste "Activités récentes"
3. ✅ Données affichées

### Test 3: Élèves ✅
1. Cliquer sur "Élèves" dans le menu
2. Voir la liste des élèves (2 élèves de test)
3. Utiliser la recherche
4. ✅ Tableau fonctionnel

### Test 4: Paiements ✅
1. Cliquer sur "Paiements"
2. Rechercher un élève : "Jean" ou "Marie"
3. Sélectionner l'élève
4. Voir ses informations et solde
5. ✅ Formulaire de paiement affiché

### Test 5: Classes ✅
1. Cliquer sur "Classes"
2. Voir les 2 classes : 6ème A et B
3. Vérifier les effectifs
4. ✅ Liste des classes affichée

---

## 🔐 Comptes de Test Disponibles

### Admin (Tout accès)
```
Username: admin
Password: admin123
```

### Comptable (Finance)
```
Username: comptable
Password: comptable123
```

### Parent (Consultation)
```
Username: parent1
Password: parent123
```

### Enseignant (Classes)
```
Username: enseignant1
Password: enseignant123
```

---

## 📊 Données de Test Disponibles

### Année Scolaire
- ✅ 2024-2025 (Active)
- ✅ 3 Trimestres

### Classes
- ✅ 6ème A (Salle A-101, Capacité 35)
- ✅ 6ème B (Salle A-102, Capacité 35)

### Élèves
- ✅ Jean Dupont (6ème A, Masculin)
- ✅ Marie Martin (6ème A, Féminin, Boursière)

### Niveaux
- ✅ 6 niveaux (1ère à 6ème)

---

## 🧪 Test de l'API avec cURL

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Résultat attendu :**
```json
{
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "user": {
    "username": "admin",
    "roles": ["ADMIN"]
  }
}
```

### 2. Récupérer les Élèves
```bash
# Remplacer <TOKEN> par le token reçu
curl http://localhost:8080/api/eleves \
  -H "Authorization: Bearer <TOKEN>"
```

### 3. Récupérer les Classes
```bash
curl http://localhost:8080/api/classes \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🔍 Vérification des Composants

### Backend ✅
- [x] Spring Boot démarre sans erreur
- [x] Base de données connectée (PostgreSQL)
- [x] 44 tables JPA créées
- [x] Données de test chargées
- [x] Endpoints API fonctionnels

### Authentification ✅
- [x] Login fonctionnel
- [x] JWT généré et validé
- [x] Tokens stockés dans localStorage
- [x] Auto-redirection si non connecté
- [x] Logout fonctionnel

### Frontend ✅
- [x] 15 pages HTML accessibles
- [x] CSS chargé correctement
- [x] JavaScript fonctionnel
- [x] API client configuré
- [x] Navigation entre pages

---

## 📱 Pages Disponibles

| Page | URL | Statut |
|------|-----|--------|
| Accueil | `/` | ✅ |
| Login | `/login.html` | ✅ |
| Dashboard | `/dashboard.html` | ✅ |
| Élèves | `/eleves.html` | ✅ |
| Paiements | `/paiements.html` | ✅ |
| Classes | `/classes.html` | ✅ |
| Parents | `/parents.html` | ✅ |
| Enseignants | `/enseignants.html` | ✅ |
| Factures | `/factures.html` | ✅ |
| Dérogations | `/derogations.html` | ✅ |
| Messages | `/messages.html` | ✅ |
| Notifications | `/notifications.html` | ✅ |
| Annonces | `/annonces.html` | ✅ |
| Rapports | `/rapports.html` | ✅ |
| Paramètres | `/parametres.html` | ✅ |

---

## 🐛 Résolution de Problèmes

### Problème: Port 8080 déjà utilisé
```bash
# Windows
netstat -ano | findstr :8080
taskkill /F /PID <PID>

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Problème: Erreur de connexion DB
**Vérifier `application.yml` :**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://...
    username: postgres
    password: ...
```

### Problème: Login ne fonctionne pas
**Vérifier :**
1. Profile dev activé : `profiles.active: dev`
2. Logs du serveur pour les erreurs
3. Console navigateur (F12) pour erreurs JS

### Problème: 401 Unauthorized
**Solution :**
```javascript
// Vérifier le token
console.log(localStorage.getItem('ecolegest_token'));

// Re-login si nécessaire
AuthService.logout();
```

---

## 📈 Prochaines Étapes

### Fonctionnalités à Développer
1. ✅ Formulaires d'ajout d'élèves
2. ✅ Génération de factures
3. ✅ Impression de reçus
4. ✅ Export Excel/PDF des rapports
5. ✅ Notifications en temps réel
6. ✅ Upload de photos
7. ✅ Gestion documentaire

### Améliorations UI
1. ✅ Intégration Chart.js pour graphiques
2. ✅ Mode sombre
3. ✅ Multi-langue
4. ✅ PWA (Progressive Web App)
5. ✅ Notifications push

---

## 📚 Documentation Complète

### Fichiers de Documentation
- `README.md` - Vue d'ensemble du projet
- `FRONTEND_README.md` - Documentation frontend
- `AUTHENTICATION_SETUP.md` - Système d'authentification
- `QUICK_START.md` - Ce guide

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Actuator Health
```
http://localhost:8080/actuator/health
```

---

## 🎯 Checklist de Déploiement

### Développement ✅
- [x] Application démarre sans erreur
- [x] Login fonctionnel
- [x] Toutes les pages accessibles
- [x] Données de test présentes
- [x] API endpoints testés

### Production 🔄
- [ ] Variables d'environnement configurées
- [ ] HTTPS activé
- [ ] Base de données de production
- [ ] Backup automatique configuré
- [ ] Logs centralisés
- [ ] Monitoring (Prometheus/Grafana)

---

## 💡 Astuces

### Développement
```bash
# Hot reload (avec Spring DevTools)
mvn spring-boot:run

# Mode debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Logs
```bash
# Voir les logs en temps réel
tail -f logs/application.log

# Niveau de log
logging.level.com.gescom.ecole=DEBUG
```

### Performance
```bash
# Profiling
java -jar target/*.jar --spring.profiles.active=dev -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

---

## 🏁 Résumé

### ✅ Ce qui Fonctionne
1. ✅ Backend Spring Boot opérationnel
2. ✅ Authentification JWT complète
3. ✅ Frontend avec 15 pages
4. ✅ Données de test chargées
5. ✅ Navigation fonctionnelle
6. ✅ API REST accessible
7. ✅ Sécurité configurée

### 🎉 L'Application est Prête !
```
http://localhost:8080/
Username: admin
Password: admin123
```

---

**Bon développement ! 🚀**
