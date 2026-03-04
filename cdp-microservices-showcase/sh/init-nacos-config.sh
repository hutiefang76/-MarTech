#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
CONFIG_DIR="$PROJECT_DIR/middleware/nacos-config"
NACOS_ADDR="${NACOS_ADDR:-http://127.0.0.1:8848}"
GROUP="${NACOS_GROUP:-DEFAULT_GROUP}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl not found, skip nacos config init"
  exit 0
fi

echo "init nacos configs from $CONFIG_DIR"
for file in "$CONFIG_DIR"/*.yaml; do
  [ -f "$file" ] || continue
  data_id="$(basename "$file")"
  content="$(cat "$file")"
  curl -sS -X POST "$NACOS_ADDR/nacos/v1/cs/configs" \
    --data-urlencode "dataId=$data_id" \
    --data-urlencode "group=$GROUP" \
    --data-urlencode "type=yaml" \
    --data-urlencode "content=$content" >/dev/null
  echo "published: $data_id"
done

echo "nacos config init completed"
