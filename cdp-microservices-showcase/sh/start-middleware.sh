#!/usr/bin/env bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_DIR"

WITH_OBSERVABILITY="${1:-}"
PROFILE_ARGS=()

mkdir -p runtime-data/mysql/data
mkdir -p runtime-data/redis/data
mkdir -p runtime-data/nacos/data runtime-data/nacos/logs
mkdir -p runtime-data/kafka/data
mkdir -p runtime-data/skywalking/oap
mkdir -p runtime-data/elasticsearch/data
mkdir -p runtime-data/logstash/data
mkdir -p runtime-data/filebeat/data

if [[ "$WITH_OBSERVABILITY" == "--with-observability" ]]; then
  PROFILE_ARGS+=(--profile observability)
fi

docker compose -f middleware/docker-compose.yml "${PROFILE_ARGS[@]}" up -d

echo "middleware started"
echo "core: mysql redis nacos kafka"
if [[ "$WITH_OBSERVABILITY" == "--with-observability" ]]; then
  echo "observability: skywalking/otel-collector/elk/dozzle"
fi
echo "run sh/init-nacos-config.sh to preload config center entries"
