# API Documentation - Gestion École Backend

## Base URL
- Development: `http://localhost:8080/api`
- Production: `https://api.gestion-ecole.com`

## Authentication
L'API utilise JWT (JSON Web Tokens) pour l'authentification.

### Obtenir un token
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Réponse:
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
    "roles": ["ADMIN"]
  }
}
```

### Utiliser le token
Inclure le token dans l'en-tête Authorization:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Endpoints principaux

### 1. Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/login` | Connexion |
| POST | `/auth/refresh` | Rafraîchir le token |
| POST | `/auth/logout` | Déconnexion |
| POST | `/auth/forgot-password` | Mot de passe oublié |
| POST | `/auth/reset-password` | Réinitialiser le mot de passe |

### 2. Élèves

| Méthode | Endpoint | Description | Rôles requis |
|---------|----------|-------------|--------------|
| GET | `/eleves` | Liste des élèves | ADMIN, SECRETAIRE, ENSEIGNANT |
| GET | `/eleves/{id}` | Détails d'un élève | ADMIN, SECRETAIRE, ENSEIGNANT, PARENT |
| POST | `/eleves` | Créer un élève | ADMIN, SECRETAIRE |
| PUT | `/eleves/{id}` | Modifier un élève | ADMIN, SECRETAIRE |
| DELETE | `/eleves/{id}` | Supprimer un élève | ADMIN |
| GET | `/eleves/search?search={term}` | Rechercher des élèves | ADMIN, SECRETAIRE, ENSEIGNANT |
| GET | `/eleves/classe/{classeId}` | Élèves d'une classe | ADMIN, SECRETAIRE, ENSEIGNANT |
| POST | `/eleves/{id}/inscrire/{classeId}` | Inscrire dans une classe | ADMIN, SECRETAIRE |

### 3. Classes

| Méthode | Endpoint | Description | Rôles requis |
|---------|----------|-------------|--------------|
| GET | `/classes` | Liste des classes | ADMIN, SECRETAIRE, ENSEIGNANT |
| GET | `/classes/{id}` | Détails d'une classe | ADMIN, SECRETAIRE, ENSEIGNANT |
| POST | `/classes` | Créer une classe | ADMIN, SECRETAIRE |
| PUT | `/classes/{id}` | Modifier une classe | ADMIN, SECRETAIRE |
| DELETE | `/classes/{id}` | Supprimer une classe | ADMIN |
| GET | `/classes/actives` | Classes actives | ADMIN, SECRETAIRE, ENSEIGNANT |
| GET | `/classes/disponibles` | Classes avec places | ADMIN, SECRETAIRE |

### 4. Factures

| Méthode | Endpoint | Description | Rôles requis |
|---------|----------|-------------|--------------|
| GET | `/factures` | Liste des factures | ADMIN, COMPTABLE |
| GET | `/factures/{id}` | Détails d'une facture | ADMIN, COMPTABLE, PARENT |
| POST | `/factures` | Créer une facture | ADMIN, COMPTABLE |
| PUT | `/factures/{id}` | Modifier une facture | ADMIN, COMPTABLE |
| DELETE | `/factures/{id}` | Supprimer une facture | ADMIN |
| GET | `/factures/eleve/{eleveId}` | Factures d'un élève | ADMIN, COMPTABLE, PARENT |
| POST | `/factures/{id}/valider` | Valider une facture | ADMIN, COMPTABLE |
| POST | `/factures/{id}/rappel` | Envoyer un rappel | ADMIN, COMPTABLE |

### 5. Paiements

| Méthode | Endpoint | Description | Rôles requis |
|---------|----------|-------------|--------------|
| GET | `/paiements` | Liste des paiements | ADMIN, COMPTABLE |
| GET | `/paiements/{id}` | Détails d'un paiement | ADMIN, COMPTABLE, PARENT |
| POST | `/paiements` | Enregistrer un paiement | ADMIN, COMPTABLE, PARENT |
| PUT | `/paiements/{id}` | Modifier un paiement | ADMIN, COMPTABLE |
| DELETE | `/paiements/{id}` | Supprimer un paiement | ADMIN |
| POST | `/paiements/{id}/valider` | Valider un paiement | ADMIN, COMPTABLE |
| GET | `/paiements/{id}/recu` | Télécharger le reçu | ADMIN, COMPTABLE, PARENT |

### 6. Dashboard

| Méthode | Endpoint | Description | Rôles requis |
|---------|----------|-------------|--------------|
| GET | `/dashboard/stats` | Statistiques générales | ADMIN, COMPTABLE, SECRETAIRE |
| GET | `/dashboard/finance` | Statistiques financières | ADMIN, COMPTABLE |
| GET | `/dashboard/recent-activities` | Activités récentes | ADMIN, COMPTABLE, SECRETAIRE |
| GET | `/dashboard/summary` | Résumé complet | ADMIN, COMPTABLE, SECRETAIRE |

## Modèles de données

### Élève
```json
{
  "id": 1,
  "matricule": "ELV-2024-0001",
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "2012-05-15",
  "genre": "MASCULIN",
  "statut": "ACTIF",
  "classeId": 1,
  "parentIds": [1, 2]
}
```

### Facture
```json
{
  "id": 1,
  "numeroFacture": "FACT-202401-00001",
  "dateEmission": "2024-01-15",
  "dateEcheance": "2024-02-15",
  "montantTotal": 150000,
  "montantPaye": 50000,
  "montantRestant": 100000,
  "statut": "PARTIELLEMENT_PAYEE",
  "eleveId": 1,
  "anneeScolaireId": 1,
  "periodeId": 1
}
```

### Paiement
```json
{
  "id": 1,
  "numeroPaiement": "PAY-20240115-00001",
  "montant": 50000,
  "datePaiement": "2024-01-15T10:30:00",
  "modePaiement": "ESPECES",
  "statut": "VALIDE",
  "factureId": 1,
  "parentId": 1
}
```

## Codes d'erreur

| Code | Description |
|------|-------------|
| 200 | OK - Requête réussie |
| 201 | Created - Ressource créée |
| 204 | No Content - Suppression réussie |
| 400 | Bad Request - Données invalides |
| 401 | Unauthorized - Non authentifié |
| 403 | Forbidden - Accès refusé |
| 404 | Not Found - Ressource non trouvée |
| 409 | Conflict - Conflit (ex: doublon) |
| 500 | Internal Server Error - Erreur serveur |

## Format des erreurs

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Les données fournies sont invalides",
  "path": "/api/eleves",
  "validationErrors": {
    "nom": "Le nom est obligatoire",
    "dateNaissance": "La date de naissance doit être dans le passé"
  }
}
```

## Pagination

Les endpoints qui retournent des listes supportent la pagination:

```http
GET /api/eleves?page=0&size=20&sort=nom,asc
```

Paramètres:
- `page`: Numéro de page (commence à 0)
- `size`: Nombre d'éléments par page
- `sort`: Tri (format: `field,direction`)

Réponse paginée:
```json
{
  "content": [...],
  "pageable": {
    "sort": {...},
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

## Exemples d'utilisation

### Créer un élève
```bash
curl -X POST http://localhost:8080/api/eleves \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Martin",
    "prenom": "Sophie",
    "dateNaissance": "2013-03-20",
    "genre": "FEMININ",
    "classeId": 1
  }'
```

### Enregistrer un paiement
```bash
curl -X POST http://localhost:8080/api/paiements \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "montant": 50000,
    "modePaiement": "MOBILE_MONEY",
    "factureId": 1,
    "numeroTransaction": "MM123456789"
  }'
```

## Swagger UI

La documentation interactive est disponible à:
- Development: http://localhost:8080/api/swagger-ui.html
- Production: Désactivé pour des raisons de sécurité

## Support

Pour toute question sur l'API: api-support@gestion-ecole.com
