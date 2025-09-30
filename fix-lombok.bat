@echo off
echo ========================================
echo Configuration de Lombok pour le projet
echo ========================================
echo.

echo [1] Téléchargement de lombok.jar...
curl -L https://projectlombok.org/downloads/lombok.jar -o lombok.jar

echo.
echo [2] Installation de Lombok...
java -jar lombok.jar

echo.
echo [3] Nettoyage du cache Maven...
call mvn clean

echo.
echo [4] Recompilation avec Lombok...
call mvn compile -DskipTests

echo.
echo Configuration terminée!
echo Redémarrez votre IDE pour que les changements prennent effet.
pause
