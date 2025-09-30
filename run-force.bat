@echo off
echo ========================================
echo Demarrage force du backend Spring Boot
echo ========================================
echo.

echo [1] Tentative de compilation...
call mvn clean compile -DskipTests -fn

echo.
echo [2] Tentative de lancement...
call mvn spring-boot:run -DskipTests -Dspring-boot.run.profiles=dev -fn

echo.
echo Application terminee.
pause
