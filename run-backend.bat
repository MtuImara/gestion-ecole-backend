@echo off
echo ========================================
echo Demarrage du Backend Spring Boot
echo ========================================
echo.

echo [1] Compilation avec tolerance d'erreurs...
call mvn clean compile -fn -DskipTests

echo.
echo [2] Packaging...
call mvn package -fn -DskipTests

echo.
echo [3] Lancement de l'application...
java -jar target/gestion-ecole-backend-1.0.0.jar

pause
