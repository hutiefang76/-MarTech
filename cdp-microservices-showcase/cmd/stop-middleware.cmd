@echo off
setlocal
cd /d %~dp0\..
docker compose -f middleware\docker-compose.yml down
if errorlevel 1 exit /b 1
echo middleware stopped
