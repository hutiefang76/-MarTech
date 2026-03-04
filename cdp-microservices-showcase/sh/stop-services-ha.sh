#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PID_DIR="$PROJECT_DIR/runtime-data/pids"

if [[ ! -d "$PID_DIR" ]]; then
  echo "no pid directory found"
  exit 0
fi

for pid_file in "$PID_DIR"/*.pid; do
  [[ -f "$pid_file" ]] || continue
  pid="$(cat "$pid_file")"
  if kill -0 "$pid" >/dev/null 2>&1; then
    kill "$pid" || true
    echo "stopped pid=$pid ($pid_file)"
  fi
  rm -f "$pid_file"
done

echo "ha demo services stopped"
