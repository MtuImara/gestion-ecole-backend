#!/bin/bash

echo "========================================"
echo "   GESTION ECOLE - BACKEND"
echo "========================================"
echo

echo "[1] Nettoyage du projet..."
mvn clean

echo
echo "[2] Compilation et packaging..."
mvn package -DskipTests

echo
echo "[3] Démarrage de l'application..."
echo
echo "Application disponible sur : http://localhost:8080"
echo "Documentation Swagger : http://localhost:8080/swagger-ui.html"
echo "Page de connexion : http://localhost:8080/login.html"
echo
echo "Comptes de test :"
echo "- Admin : admin / admin123"
echo "- Comptable : comptable / comptable123"
echo "- Parent : parent1 / parent123"
echo "- Enseignant : enseignant1 / enseignant123"
echo
echo "========================================"
echo

java -jar target/gestion-ecole-backend-1.0.0.jar
