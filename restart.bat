@echo off
echo ====================================
echo   REDEMARRAGE DU SERVEUR
echo ====================================
echo.
echo Compilation du projet...
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR: La compilation a echoue!
    pause
    exit /b 1
)
echo.
echo Compilation reussie!
echo.
echo Demarrage du serveur...
mvn spring-boot:run
pause
