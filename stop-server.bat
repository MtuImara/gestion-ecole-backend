@echo off
echo ========================================
echo   ARRET DU SERVEUR GESTION ECOLE
echo ========================================
echo.
echo Recherche du processus Java en cours...
for /f "tokens=2" %%i in ('tasklist ^| findstr "java.exe"') do (
    echo Arret du processus Java PID: %%i
    taskkill /F /PID %%i
)
echo.
echo Serveur arrete!
pause
