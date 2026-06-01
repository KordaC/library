@echo off
setlocal EnableExtensions

echo.
echo === Cloudflare Tunnel (альтернатива ngrok) ===
echo.

where cloudflared >nul 2>&1
if errorlevel 1 (
    echo [Ошибка] cloudflared не найден в PATH.
    echo.
    echo Установка:
    echo   winget install Cloudflare.cloudflared
    echo или скачайте: https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads/
    echo Добавьте папку с cloudflared.exe в PATH.
    goto :done
)

echo Backend должен быть запущен на http://127.0.0.1:8080
echo.
echo Ниже появится строка вида:
echo   https://xxxx.trycloudflare.com
echo.
echo Скопируйте этот HTTPS-адрес, затем:
echo   1. $env:LIBRARY_PUBLIC_BASE_URL="https://xxxx.trycloudflare.com"
echo   2. Перезапустите backend
echo   3. В приложении: https://xxxx.trycloudflare.com/api/v1/
echo.

cloudflared tunnel --url http://127.0.0.1:8080

:done
echo.
pause
