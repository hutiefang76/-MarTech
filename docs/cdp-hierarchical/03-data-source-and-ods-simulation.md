# 03 - Data Source And ODS Simulation

## 1. 目标

回答三个核心问题：

1. 四类数据源任务是否具备。
2. 任务触发后是否能看到 ODS 结果。
3. 是否可通过页面直接操作。

答案：在当前实现中，以上三点都支持（模拟实现）。

## 2. 四类数据源任务准备情况

### 2.1 MySQL 关系库

- 语义：模拟 MySQL CDC（Flink CDC 风格）。
- 入口：`/demo/console/simulate/import/mysql-cdc`。

### 2.2 Kafka 流数据

- 语义：模拟 Flink 实时消费 Kafka。
- 入口：`/demo/console/simulate/import/kafka`。

### 2.3 ES 数据源

- 语义：模拟 ES 增量采集任务。
- 入口：`/demo/console/simulate/import/es`。
- 说明：当前实现统一走 Flink 模拟任务，不单独引入 Spark。

### 2.4 文件系统

- 语义：模拟定时批量文件采集。
- 入口：`/demo/console/simulate/import/file`。

## 3. ODS 模拟处理流程

`页面点击 -> callchain-demo -> connector-control -> flink-job -> 生成随机事件 -> ODS 存储`

### 3.1 触发

`connector-control` 把导入请求转换为 Flink 任务提交请求。

### 3.2 任务状态

`flink-job-service` 创建任务并维护状态机（RUNNING/SUCCESS/FAILED）。

### 3.3 数据生成

`flink-job-service` 会按任务类型生成随机模拟事件。

### 3.4 数据落地

1. MySQL 可用：写入 `cdp_ods_simulated_event`。
2. MySQL 不可用：回退内存队列，仍可查询最新模拟结果。

## 4. ODS 查询接口

1. 汇总：`GET /flink/jobs/ods/summary`
2. 最新：`GET /flink/jobs/ods/latest?sourceType=...&limit=...`
3. 按任务：`GET /flink/jobs/ods/job/{jobId}?limit=...`

演示层转发接口：

1. `GET /demo/console/ods/summary`
2. `GET /demo/console/ods/latest`
3. `GET /demo/console/ods/job/{jobId}`

## 5. 页面操作能力

演示页面支持：

1. 一键触发四类数据源任务。
2. 查询任务状态。
3. 手工推进任务状态。
4. 查询 ODS 汇总、最新数据、按任务明细。

页面地址：`http://localhost:19180/`

## 6. 与真实生产的关系

当前方案在语义上模拟了真实链路：

1. 上游多源接入。
2. 实时或批量任务触发。
3. ODS 层持续沉淀。
4. 状态可观测和结果可回查。

实际生产中可以把模拟数据生成替换为真实 Flink Job 与真实上游连接器。
