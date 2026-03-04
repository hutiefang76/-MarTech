@echo off
setlocal
cd /d %~dp0\..

set OBS=
if /I "%1"=="--with-observability" set OBS=--profile observability

if not exist runtime-data\mysql\data mkdir runtime-data\mysql\data
if not exist runtime-data\redis\data mkdir runtime-data\redis\data
if not exist runtime-data\nacos\data mkdir runtime-data\nacos\data
if not exist runtime-data\nacos\logs mkdir runtime-data\nacos\logs
if not exist runtime-data\kafka\data mkdir runtime-data\kafka\data
if not exist runtime-data\skywalking\oap mkdir runtime-data\skywalking\oap
if not exist runtime-data\elasticsearch\data mkdir runtime-data\elasticsearch\data
if not exist runtime-data\logstash\data mkdir runtime-data\logstash\data
if not exist runtime-data\filebeat\data mkdir runtime-data\filebeat\data

docker compose -f middleware\docker-compose.yml %OBS% up -d
if errorlevel 1 exit /b 1
echo middleware started
echo core: mysql redis nacos kafka
if /I "%1"=="--with-observability" echo observability: skywalking/otel-collector/elk/dozzle
echo run cmd\init-nacos-config.cmd to preload config center entries
