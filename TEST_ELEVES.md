# Guide de Test - Page Élèves

## 🚀 Démarrage

1. **Lancer l'application** :
   ```bash
   mvn spring-boot:run
   ```

2. **Accéder à la page** :
   - URL : http://localhost:8080/eleves.html
   - Connexion : admin / admin123

## ✅ Fonctionnalités à tester

### 1. **Affichage de la liste**
- ✅ La page doit afficher un tableau avec les élèves
- ✅ Les statistiques (Total, Actifs, Boursiers) doivent s'afficher
- ✅ La pagination doit fonctionner si > 10 élèves

### 2. **Recherche et filtres**
- ✅ Recherche par nom/prénom/matricule
- ✅ Filtre par classe (6A, 6B, 5A, 5B, 4, 3)
- ✅ Filtre par statut (Actif/Inactif)
- ✅ Bouton "Réinitialiser" pour effacer les filtres

### 3. **Ajout d'un élève**
- ✅ Cliquer sur "➕ Nouvel Élève"
- ✅ Le modal doit s'ouvrir
- ✅ Bouton "Générer" pour le matricule automatique
- ✅ Remplir tous les champs requis :
  - Matricule (auto-généré)
  - Nom et Prénom
  - Date de naissance
  - Genre (M/F)
  - Classe
  - Nom et téléphone du parent
- ✅ Cliquer sur "Enregistrer"
- ✅ Notification de succès
- ✅ L'élève apparaît dans la liste

### 4. **Modification d'un élève**
- ✅ Cliquer sur le bouton ✏️ (Modifier)
- ✅ Le modal s'ouvre avec les données pré-remplies
- ✅ Modifier les informations
- ✅ Enregistrer les changements
- ✅ Vérifier la mise à jour dans la liste

### 5. **Suppression d'un élève**
- ✅ Cliquer sur le bouton 🗑️ (Supprimer)
- ✅ Confirmer la suppression
- ✅ L'élève disparaît de la liste
- ✅ Notification de succès

### 6. **Visualisation des détails**
- ✅ Cliquer sur le bouton 👁️ (Voir)
- ✅ Affichage des détails complets de l'élève

## 🔧 Résolution des problèmes

### Si le modal ne s'ouvre pas :
1. Ouvrir la console du navigateur (F12)
2. Vérifier les erreurs JavaScript
3. Rafraîchir la page (F5)

### Si les données ne se chargent pas :
1. Vérifier que le backend est lancé
2. Vérifier la console pour les erreurs API
3. Le système bascule automatiquement en mode démo si l'API n'est pas disponible

### Si les boutons ne fonctionnent pas :
1. Vider le cache du navigateur (Ctrl+Shift+R)
2. Vérifier que JavaScript est activé
3. Tester dans un autre navigateur

## 📊 Mode Démonstration

Si l'API backend n'est pas disponible, l'application fonctionne avec des données de test :
- 30 élèves générés automatiquement
- Toutes les fonctionnalités CRUD fonctionnent localement
- Message "Mode hors ligne" affiché

## 🎯 Points d'attention

1. **Matricule** : Généré automatiquement (ELV0001, ELV0002, etc.)
2. **Classes disponibles** : 6A, 6B, 5A, 5B, 4, 3
3. **Statuts** : ACTIF ou INACTIF
4. **Pagination** : 10 élèves par page

## 📝 Notes

- Les modifications sont sauvegardées en temps réel
- Les notifications apparaissent en haut à droite
- La recherche est instantanée (pas besoin d'appuyer sur Entrée)
- Les filtres peuvent être combinés
