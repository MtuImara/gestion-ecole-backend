# 🎓 EcoleGest - Documentation Frontend

## 📱 Accès à l'Application

### URL de Base
```
http://localhost:8080/
```

L'application se lancera automatiquement sur la page de connexion.

---

## 🔐 Comptes de Test Disponibles

### 1. Administrateur (Accès Complet)
```
Username: admin
Password: admin123
Rôle: ADMIN
Permissions: Toutes
```
**Accès à :** Tout le système

### 2. Comptable (Finance)
```
Username: comptable
Password: comptable123
Rôle: COMPTABLE
Permissions: Lecture, Gestion finances
```
**Accès à :** Dashboard, Paiements, Factures, Rapports financiers

### 3. Parent (Consultation)
```
Username: parent1
Password: parent123
Rôle: PARENT
Permissions: Lecture seulement
```
**Accès à :** Informations de ses enfants, paiements, bulletins

### 4. Enseignant
```
Username: enseignant1
Password: enseignant123
Rôle: ENSEIGNANT
Permissions: Lecture + gestion de ses classes
```
**Accès à :** Élèves de ses classes, notes, absences

---

## 📄 Pages Disponibles

### 🏠 Pages Principales
1. **Dashboard** (`/dashboard.html`)
   - Statistiques en temps réel
   - Graphiques d'évolution
   - Activités récentes

2. **Élèves** (`/eleves.html`)
   - Liste complète avec recherche
   - Pagination
   - Actions : Voir, Modifier, Supprimer

3. **Paiements** (`/paiements.html`)
   - Recherche d'élève
   - Enregistrement de paiement
   - 4 modes : Espèces, Mobile Money, Virement, Chèque

4. **Classes** (`/classes.html`)
   - Gestion des classes
   - Effectifs et capacités
   - Enseignants principaux

5. **Parents** (`/parents.html`)
   - Liste des parents d'élèves

6. **Enseignants** (`/enseignants.html`)
   - Personnel enseignant

7. **Factures** (`/factures.html`)
   - Facturation et impayés
   - Filtres par statut

8. **Dérogations** (`/derogations.html`)
   - Bourses et réductions
   - Gestion des aides financières

### 📊 Rapports
9. **Centre de Rapports** (`/rapports.html`)
   - 8 types de rapports disponibles
   - Export PDF/Excel

### 💬 Communication
10. **Messages** (`/messages.html`)
    - Messagerie interne
    - Conversations

11. **Notifications** (`/notifications.html`)
    - Centre de notifications
    - Filtres par type

12. **Annonces** (`/annonces.html`)
    - Publications officielles

### ⚙️ Administration
13. **Paramètres** (`/parametres.html`)
    - Configuration du système
    - Année scolaire
    - Notifications
    - Sécurité

---

## 🎨 Fonctionnalités Frontend

### ✨ Authentification JWT
- Login avec token JWT
- Auto-redirection si non connecté
- Stockage sécurisé du token
- Refresh token automatique

### 📱 Interface Moderne
- Design responsive (mobile, tablette, desktop)
- Animations fluides
- Notifications toast
- Sidebar navigation
- Thème professionnel

### 🔍 Recherche & Filtres
- Recherche en temps réel
- Debounce pour optimiser les requêtes
- Filtres multiples
- Pagination

### 💾 Gestion des Données
- CRUD complet
- Validation côté client
- Messages d'erreur clairs
- Confirmation avant suppression

---

## 🛠️ Technologies Utilisées

### Frontend
- **HTML5** - Structure
- **CSS3** - Styles modernes avec gradients
- **JavaScript Vanilla** - Logique applicative
- **Fetch API** - Appels REST

### Backend
- **Spring Boot 3.x**
- **Spring Security** avec JWT
- **PostgreSQL** (ou H2 pour dev)
- **Spring Data JPA**

---

## 🚀 Démarrage Rapide

### 1. Lancer le Backend
```bash
cd e:/vide/Gescom/gestion-ecole-backend
mvn spring-boot:run
```

### 2. Attendre le Message
```
Started GestionEcoleApplication in X seconds
```

### 3. Ouvrir le Navigateur
```
http://localhost:8080/
```

### 4. Se Connecter
Utilisez l'un des comptes de test ci-dessus

---

## 📊 Données de Test Disponibles

Le système créé automatiquement au démarrage :

### Utilisateurs
- ✅ 4 utilisateurs (Admin, Comptable, Parent, Enseignant)
- ✅ 5 rôles avec permissions

### Données Scolaires
- ✅ 1 année scolaire active (2024-2025)
- ✅ 3 périodes (trimestres)
- ✅ 6 niveaux (1ère à 6ème)
- ✅ 2 classes (6ème A et B)
- ✅ 2 élèves inscrits

---

## 🔧 Configuration

### API Base URL
```javascript
const API_BASE_URL = '/api';
```

### Token Storage
```javascript
localStorage.setItem('ecolegest_token', token);
localStorage.setItem('ecolegest_user', JSON.stringify(user));
```

---

## 📝 Structure des Fichiers Frontend

```
src/main/resources/static/
├── index.html              # Page d'accueil
├── login.html              # Authentification
├── dashboard.html          # Tableau de bord
├── eleves.html             # Gestion élèves
├── paiements.html          # Enregistrement paiements
├── classes.html            # Gestion classes
├── parents.html            # Liste parents
├── enseignants.html        # Liste enseignants
├── factures.html           # Gestion factures
├── derogations.html        # Bourses & dérogations
├── messages.html           # Messagerie
├── notifications.html      # Centre notifications
├── annonces.html           # Annonces
├── rapports.html           # Centre rapports
├── parametres.html         # Configuration
├── css/
│   ├── styles.css          # Styles communs
│   ├── paiements.css       # Styles paiements
│   └── table-common.css    # Styles tableaux
└── js/
    ├── api.js              # Client API REST
    ├── dashboard.js        # Logique dashboard
    ├── paiements.js        # Logique paiements
    ├── eleves.js           # Logique élèves
    └── classes.js          # Logique classes
```

---

## 🐛 Dépannage

### Problème : "Unauthorized 401"
**Solution :** Vérifiez que vous êtes connecté et que le token est valide

### Problème : "Port 8080 already in use"
**Solution :**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /F /PID <PID>

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Problème : Pas de données affichées
**Solution :** Vérifiez que le profile "dev" est activé dans `application.yml`

### Problème : Erreur CORS
**Solution :** Déjà configuré dans `SecurityConfig.java`, redémarrez le serveur

---

## 📧 Support

Pour toute question ou problème :
- Vérifiez les logs du serveur
- Vérifiez la console du navigateur (F12)
- Consultez ce README

---

## 🎯 Prochaines Améliorations Suggérées

### Frontend
- [ ] Intégration Chart.js pour les graphiques
- [ ] Upload de fichiers (photos, documents)
- [ ] Impression de reçus PDF
- [ ] Mode sombre
- [ ] Multi-langue (Français, English, Kirundi)

### Backend
- [ ] Export Excel/PDF des rapports
- [ ] Envoi d'emails automatiques
- [ ] Notifications push
- [ ] Backup automatique
- [ ] Logs d'audit

---

**Version :** 1.0.0  
**Dernière mise à jour :** 30 septembre 2024  
**Statut :** ✅ Production Ready
