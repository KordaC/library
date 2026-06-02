@echo off
chcp 65001 >nul
cd /d "%~dp0.."
echo.
echo === APK для мобильного интернета (release) ===
echo URL берётся из app\cloud-api.properties или local.properties backend.url
echo.
call gradlew.bat :app:assembleRelease
if errorlevel 1 (
    echo Сборка не удалась.
    pause
    exit /b 1
)
echo.
echo Готово: app\build\outputs\apk\release\app-release.apk
echo Установите на телефон, LTE, вход: 20001 / Demo1234
echo.
pause
