@echo off
setlocal EnableExtensions

echo.
echo === Туннель ngrok для библиотеки ===
echo.

where ngrok >nul 2>&1
if errorlevel 1 (
    echo [Ошибка] ngrok не найден в PATH.
    echo Скачайте: https://ngrok.com/download
    echo.
    echo Если ngrok пишет ERR_NGROK_9040 (IP заблокирован) — используйте:
    echo   scripts\start-tunnel-cloudflare.bat
    goto :done
)

echo Если ngrok выдаст ERR_NGROK_9040 — ваш IP недоступен для ngrok.
echo Используйте: scripts\start-tunnel-cloudflare.bat
echo.

echo Убедитесь, что backend уже запущен на порту 8080.
echo.
echo Откроется окно ngrok. Скопируйте HTTPS-адрес, например:
echo   https://xxxx.ngrok-free.app
echo.
echo Дальше:
echo   1. В PowerShell: $env:LIBRARY_PUBLIC_BASE_URL="https://xxxx.ngrok-free.app"
echo   2. Перезапустите backend (bootRun + postgres)
echo   3. В приложении адрес: https://xxxx.ngrok-free.app/api/v1/
echo.

start "ngrok-tunnel" cmd /k ngrok http 8080

:done
echo.
pause
