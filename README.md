# Gestion École Backend

API REST pour la gestion financière et administrative d'un établissement scolaire.

## 🚀 Technologies utilisées

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** avec JWT
- **Spring Data JPA** / Hibernate
- **PostgreSQL** / H2 (dev)
- **MapStruct** pour le mapping DTO
- **Lombok** pour réduire le boilerplate
- **OpenAPI 3** (Swagger) pour la documentation
- **Maven** pour la gestion des dépendances

## 📋 Fonctionnalités principales

### 👥 Gestion des utilisateurs
- Authentification JWT avec refresh token
- Gestion des rôles et permissions
- Types d'utilisateurs : Admin, Comptable, Parent, Enseignant
- Réinitialisation de mot de passe

### 🎓 Gestion scolaire
- Années scolaires et périodes
- Niveaux et classes
- Inscription et gestion des élèves
- Transfert d'élèves entre classes
- Gestion des enseignants

### 💰 Gestion financière
- Configuration des frais scolaires
- Génération de factures
- Enregistrement des paiements (multi-modes)
- Gestion des reçus
- Échéanciers de paiement
- Rappels automatiques

### 🎯 Cas spéciaux
- Dérogations de paiement
- Gestion des bourses
- Réductions (fratrie, excellence, sociale)
- Cas d'insolvabilité
- Plans de remboursement

### 📊 Reporting
- Tableaux de bord
- Rapports financiers
- Statistiques par classe/niveau
- Export PDF/Excel

## 🛠️ Installation

### Prérequis
- JDK 17 ou supérieur
- Maven 3.8+
- PostgreSQL 14+ (ou utiliser H2 pour le développement)

### Configuration de la base de données

1. Créer une base de données PostgreSQL :
```sql
CREATE DATABASE gestion_ecole_db;
```

2. Configurer les variables d'environnement ou modifier `application.yml` :
```yaml
DB_USERNAME=postgres
DB_PASSWORD=votre_mot_de_passe
```

### Lancement de l'application

```bash
# Cloner le repository
git clone [url-du-repo]
cd gestion-ecole-backend

# Installer les dépendances
mvn clean install

# Lancer l'application
mvn spring-boot:run

# Ou avec un profil spécifique
mvn spring-boot:run -Dspring.profiles.active=dev
```

L'application sera accessible sur `http://localhost:8080/api`

## 📚 Documentation API

La documentation Swagger est disponible à : `http://localhost:8080/api/swagger-ui.html`

### Endpoints principaux

#### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/refresh` - Rafraîchir le token
- `POST /api/auth/logout` - Déconnexion
- `POST /api/auth/forgot-password` - Mot de passe oublié
- `POST /api/auth/reset-password` - Réinitialiser le mot de passe

#### Élèves
- `GET /api/eleves` - Liste des élèves
- `GET /api/eleves/{id}` - Détails d'un élève
- `POST /api/eleves` - Créer un élève
- `PUT /api/eleves/{id}` - Modifier un élève
- `DELETE /api/eleves/{id}` - Supprimer un élève
- `POST /api/eleves/{id}/inscrire/{classeId}` - Inscrire dans une classe
- `POST /api/eleves/{id}/transferer/{classeId}` - Transférer vers une autre classe

#### Factures
- `GET /api/factures` - Liste des factures
- `GET /api/factures/{id}` - Détails d'une facture
- `POST /api/factures` - Créer une facture
- `GET /api/factures/eleve/{eleveId}` - Factures d'un élève
- `POST /api/factures/{id}/rappel` - Envoyer un rappel

#### Paiements
- `GET /api/paiements` - Liste des paiements
- `POST /api/paiements` - Enregistrer un paiement
- `POST /api/paiements/{id}/valider` - Valider un paiement
- `POST /api/paiements/{id}/annuler` - Annuler un paiement
- `GET /api/paiements/{id}/recu` - Télécharger le reçu

## 🔒 Sécurité

### JWT Configuration
- Token d'accès : 1 heure
- Refresh token : 30 jours
- Secret key configurable via variable d'environnement

### CORS
Configuré pour accepter les requêtes depuis :
- `http://localhost:3000` (React/Vue dev)
- `http://localhost:5173` (Vite dev)

### Rôles et permissions
- **ADMIN** : Accès complet
- **COMPTABLE** : Gestion financière
- **SECRETAIRE** : Gestion administrative
- **ENSEIGNANT** : Consultation classes et élèves
- **PARENT** : Consultation et paiement

## 🏗️ Architecture

```
src/main/java/com/gescom/ecole/
├── common/
│   ├── entity/         # Entité de base
│   └── enums/          # Énumérations
├── config/
│   └── security/       # Configuration sécurité
├── controller/         # Controllers REST
├── dto/               # Data Transfer Objects
├── entity/            # Entités JPA
│   ├── finance/       # Entités financières
│   ├── scolaire/      # Entités scolaires
│   └── utilisateur/   # Entités utilisateurs
├── mapper/            # Mappers MapStruct
├── repository/        # Repositories JPA
├── service/           # Services métier
│   └── impl/          # Implémentations
└── GestionEcoleApplication.java
```

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Tests avec couverture
mvn test jacoco:report
```

## 📦 Build et déploiement

```bash
# Build pour production
mvn clean package -P prod

# Le JAR sera généré dans target/
java -jar target/gestion-ecole-backend-1.0.0.jar
```

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/gestion-ecole-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# Build image Docker
docker build -t gestion-ecole-backend .

# Lancer le conteneur
docker run -p 8080:8080 \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  gestion-ecole-backend
```

## 🔧 Variables d'environnement

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SERVER_PORT` | Port du serveur | 8080 |
| `DB_USERNAME` | Utilisateur DB | postgres |
| `DB_PASSWORD` | Mot de passe DB | postgres |
| `JWT_SECRET` | Secret JWT | (généré) |
| `MAIL_HOST` | Serveur SMTP | smtp.gmail.com |
| `MAIL_USERNAME` | Email d'envoi | - |
| `MAIL_PASSWORD` | Mot de passe email | - |

## 📈 Monitoring

- Health check : `/api/actuator/health`
- Metrics : `/api/actuator/metrics`
- Info : `/api/actuator/info`

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 License

Ce projet est sous licence MIT.

## 👥 Équipe

- Backend : Spring Boot Team
- Frontend : Vue.js/React Team
- Mobile : Flutter Team

## 📞 Support

Pour toute question ou support : support@gestion-ecole.com
