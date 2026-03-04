# 06 - API Reference (Demo Scope)

## 1. 演示入口服务 `cdp-callchain-demo`

### 1.1 拓扑与链路查询

1. `GET /demo/console/topology`

### 1.2 导入模拟

1. `POST /demo/console/simulate/import`
2. `POST /demo/console/simulate/import/mysql-cdc`
3. `POST /demo/console/simulate/import/kafka`
4. `POST /demo/console/simulate/import/es`
5. `POST /demo/console/simulate/import/file`

### 1.3 标签任务

1. `POST /demo/console/simulate/tag-realtime`
2. `GET /demo/console/tag-task/{taskId}`

### 1.4 Flink 任务状态

1. `GET /demo/console/flink/{jobId}`
2. `POST /demo/console/flink/{jobId}/mark?status=...&detail=...`

### 1.5 ODS 查询

1. `GET /demo/console/ods/summary`
2. `GET /demo/console/ods/latest?sourceType=...&limit=...`
3. `GET /demo/console/ods/job/{jobId}?limit=...`

### 1.6 一键链路

1. `POST /demo/console/simulate/full-chain`

## 2. 任务控制服务 `cdp-connector-control-service`

1. `GET /connector/control/ping`
2. `POST /connector/control/import/deploy-k8s`

请求体核心字段：

1. `tenantId`
2. `traceId`
3. `connectorId`
4. `sourceType`
5. `targetTable`
6. `parallelism`

## 3. 标签任务服务 `cdp-tag-task-service`

1. `POST /tag/task/publish`
2. `GET /tag/task/{taskId}`
3. `GET /tag/task/list`

## 4. Flink 模拟任务服务 `cdp-flink-job-service`

### 4.1 任务管理

1. `POST /flink/jobs/deploy-k8s`
2. `GET /flink/jobs/{jobId}`
3. `GET /flink/jobs/by-biz/{bizTaskId}`
4. `POST /flink/jobs/{jobId}/mock/mark?status=...&detail=...`

### 4.2 ODS 数据查询

1. `GET /flink/jobs/ods/summary`
2. `GET /flink/jobs/ods/latest?sourceType=...&limit=...`
3. `GET /flink/jobs/ods/job/{jobId}?limit=...`

## 5. 页面入口

1. `http://localhost:19180/`

可通过页面按钮直接触发上述接口组合。
