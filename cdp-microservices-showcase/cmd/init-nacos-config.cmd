@echo off
setlocal enabledelayedexpansion
cd /d %~dp0\..

set NACOS_ADDR=http://127.0.0.1:8848
if not "%1"=="" set NACOS_ADDR=%1

for %%f in (middleware\nacos-config\*.yaml) do (
  set DATA_ID=%%~nxf
  curl -sS -X POST "%NACOS_ADDR%/nacos/v1/cs/configs" ^
    --data-urlencode "dataId=!DATA_ID!" ^
    --data-urlencode "group=DEFAULT_GROUP" ^
    --data-urlencode "type=yaml" ^
    --data-urlencode "content@%%f" >nul
  echo published: !DATA_ID!
)

echo nacos config init completed
