# Запуск ngrok для доступа к backend с мобильного интернета (LTE/5G).
# Требуется: ngrok в PATH (https://ngrok.com/download) и запущенный backend на :8080

$ErrorActionPreference = "Stop"

Write-Host "Проверка backend на http://127.0.0.1:8080 ..."
try {
    Invoke-WebRequest -Uri "http://127.0.0.1:8080/api/v1/auth/login" -Method Options -TimeoutSec 3 | Out-Null
} catch {
    Write-Host "Сначала запустите backend:" -ForegroundColor Yellow
    Write-Host "  cd backend"
    Write-Host "  .\gradlew.bat bootRun --args=`"--spring.profiles.active=postgres`""
    exit 1
}

$ngrok = Get-Command ngrok -ErrorAction SilentlyContinue
if (-not $ngrok) {
    Write-Host "Установите ngrok и добавьте в PATH: https://ngrok.com/download" -ForegroundColor Red
    exit 1
}

Write-Host "Запуск ngrok http 8080 ..."
Start-Process -FilePath "ngrok" -ArgumentList "http", "8080" -WindowStyle Normal

Start-Sleep -Seconds 4

try {
    $api = Invoke-RestMethod -Uri "http://127.0.0.1:4040/api/tunnels" -TimeoutSec 5
    $https = $api.tunnels | Where-Object { $_.public_url -like "https://*" } | Select-Object -First 1
    if (-not $https) {
        Write-Host "Туннель ещё поднимается. Откройте http://127.0.0.1:4040 и скопируйте HTTPS URL." -ForegroundColor Yellow
        exit 0
    }
    $public = $https.public_url.TrimEnd("/")
    Write-Host ""
    Write-Host "=== Публичный адрес ===" -ForegroundColor Green
    Write-Host $public
    Write-Host ""
    Write-Host "1) Перезапустите backend с переменной (в новом окне PowerShell):" -ForegroundColor Cyan
    Write-Host "   `$env:LIBRARY_PUBLIC_BASE_URL=`"$public`""
    Write-Host "   cd backend"
    Write-Host "   .\gradlew.bat bootRun --args=`"--spring.profiles.active=postgres`""
    Write-Host ""
    Write-Host "2) В приложении: Профиль -> Адрес сервера -> вставьте:" -ForegroundColor Cyan
    Write-Host "   $public/api/v1/"
    Write-Host "   (или укажите в local.properties: backend.url=$public/api/v1/ и пересоберите)"
    Write-Host ""
} catch {
    Write-Host "Не удалось прочитать API ngrok. Скопируйте HTTPS URL из окна ngrok вручную." -ForegroundColor Yellow
}
