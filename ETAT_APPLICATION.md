# État actuel de l'application

## 🔧 Configuration actuelle (MODE DEBUG)

### Sécurité
- ✅ **TOUTES les routes en `permitAll()`** - Pas d'authentification requise
- ✅ **Filtre JWT désactivé** - Commenté dans SecurityConfig
- ✅ **Mode debug activé** - Les erreurs affichent les détails réels

### Base de données
- ✅ **PostgreSQL uniquement** - H2 supprimé
- ✅ **Un seul fichier** `application.yml`
- ✅ **Connexion Railway** configurée

### APIs fonctionnelles
- `/api/auth/login` - Connexion
- `/api/eleves` - CRUD élèves
- `/api/classes` - CRUD classes
- `/api/paiements` - Gestion paiements

## 🚀 Pour démarrer

```bash
mvn spring-boot:run
```

Ou utiliser :
```bash
restart.bat
```

## 👤 Utilisateurs disponibles

| Username | Password | Rôle |
|----------|----------|------|
| admin | admin123 | Administrateur |
| comptable | comptable123 | Comptable |
| parent1 | parent123 | Parent |
| enseignant1 | enseignant123 | Enseignant |

## 📋 Classes créées automatiquement

- **6ème A** (ID: 1)
- **6ème B** (ID: 2)

## 🐛 Debug des erreurs

Le GlobalExceptionHandler affiche maintenant les erreurs réelles au lieu du message générique.

Si vous voyez une erreur, elle affichera :
- Le type d'exception
- Le message détaillé

## ⚠️ À faire pour la production

1. **Réactiver la sécurité** dans `SecurityConfig.java` :
   - Décommenter la configuration normale
   - Réactiver le filtre JWT
   
2. **Désactiver le mode debug** dans `GlobalExceptionHandler.java` :
   - Remettre le message générique pour les erreurs

3. **Configurer les logs** dans `application.yml` :
   - Passer les logs en niveau WARN ou ERROR

## 🔍 Problèmes connus et solutions

### "Full authentication is required"
**Cause** : Le filtre JWT intercepte les requêtes même si les routes sont en permitAll()
**Solution** : Filtre JWT désactivé temporairement

### "Erreur système"
**Cause** : Exception non gérée dans le backend
**Solution** : Mode debug activé pour voir l'erreur réelle

### "Cannot read properties of undefined"
**Cause** : L'API retourne undefined ou null
**Solution** : Vérifications ajoutées dans le frontend

## 📝 Notes

- L'application fonctionne sans authentification actuellement
- Toutes les APIs sont accessibles publiquement
- Les erreurs affichent les détails techniques
