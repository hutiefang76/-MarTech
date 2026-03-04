#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PID_DIR="$PROJECT_DIR/runtime-data/pids"
PROC_LOG_DIR="$PROJECT_DIR/runtime-data/process-logs"

mkdir -p "$PID_DIR" "$PROC_LOG_DIR"
cd "$PROJECT_DIR"

echo "building demo services..."
mvn -q -DskipTests -pl cdp-callchain-demo,cdp-connector-control-service,cdp-tag-task-service,cdp-flink-job-service -am package

start_instance() {
  local module="$1"
  local port="$2"
  local instance="$3"
  shift 3
  local jar="$PROJECT_DIR/$module/target/$module-1.0.0-SNAPSHOT.jar"
  local out_log="$PROC_LOG_DIR/$module-$port.out.log"
  local err_log="$PROC_LOG_DIR/$module-$port.err.log"
  local pid_file="$PID_DIR/$module-$port.pid"

  if [[ ! -f "$jar" ]]; then
    echo "jar not found: $jar"
    exit 1
  fi

  nohup java -jar "$jar" \
    --server.port="$port" \
    --APP_INSTANCE="$instance" \
    "$@" >"$out_log" 2>"$err_log" &
  echo $! > "$pid_file"
  echo "started $module port=$port pid=$(cat "$pid_file")"
}

start_instance cdp-flink-job-service 19190 1
start_instance cdp-flink-job-service 19191 2
start_instance cdp-connector-control-service 19101 1 --cdp.flink-job.base-url=http://localhost:19190
start_instance cdp-connector-control-service 19102 2 --cdp.flink-job.base-url=http://localhost:19190
start_instance cdp-tag-task-service 19141 1 --cdp.flink-job.base-url=http://localhost:19190
start_instance cdp-tag-task-service 19142 2 --cdp.flink-job.base-url=http://localhost:19190
start_instance cdp-callchain-demo 19180 1 \
  --cdp.target.connector-control-base-url=http://localhost:19101 \
  --cdp.target.tag-task-base-url=http://localhost:19141 \
  --cdp.target.flink-job-base-url=http://localhost:19190
start_instance cdp-callchain-demo 19181 2 \
  --cdp.target.connector-control-base-url=http://localhost:19101 \
  --cdp.target.tag-task-base-url=http://localhost:19141 \
  --cdp.target.flink-job-base-url=http://localhost:19190

echo "ha demo services started"
echo "nacos instance list: http://localhost:8848/nacos"
