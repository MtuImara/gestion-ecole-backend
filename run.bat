@echo off
echo ========================================
echo   GESTION ECOLE - BACKEND
echo ========================================
echo.

echo [1] Nettoyage du projet...
call mvn clean

echo.
echo [2] Compilation et packaging...
call mvn package -DskipTests

echo.
echo [3] Demarrage de l'application...
echo.
echo Application disponible sur : http://localhost:8080/api
echo Documentation Swagger : http://localhost:8080/api/swagger-ui.html
echo.
echo Comptes de test :
echo - Admin : admin / admin123
echo - Comptable : comptable / comptable123
echo - Parent : parent1 / parent123
echo - Enseignant : enseignant1 / enseignant123
echo.
echo ========================================
echo.

java -jar target/gestion-ecole-backend-1.0.0.jar

pause
