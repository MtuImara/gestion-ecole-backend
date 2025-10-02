# Script PowerShell pour nettoyer le cache du navigateur
Write-Host "🧹 Nettoyage du cache..." -ForegroundColor Cyan

# Arrêter l'application
Write-Host "Arrêt de l'application..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force

# Attendre un peu
Start-Sleep -Seconds 2

# Redémarrer l'application
Write-Host "Redémarrage de l'application..." -ForegroundColor Green
Start-Process powershell -ArgumentList "cd '$PSScriptRoot'; mvn spring-boot:run"

Write-Host ""
Write-Host "✅ Application redémarrée!" -ForegroundColor Green
Write-Host "📌 Pour forcer le rechargement dans le navigateur:" -ForegroundColor Cyan
Write-Host "   Chrome/Edge: Ctrl + Shift + R" -ForegroundColor White
Write-Host "   Firefox: Ctrl + F5" -ForegroundColor White
Write-Host ""
Write-Host "🌐 Ouvrir: http://localhost:8080/login.html" -ForegroundColor Yellow
