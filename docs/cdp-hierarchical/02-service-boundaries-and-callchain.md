# 02 - Service Boundaries And Call Chain

## 1. 服务职责边界

### 1.1 `cdp-callchain-demo`

1. 作为演示入口，承接页面按钮触发。
2. 调用下游微服务并拼接返回。
3. 不承载核心业务计算。

### 1.2 `cdp-connector-control-service`

1. 接收导入任务请求。
2. 根据 `sourceType` 映射任务类型。
3. 构造 Flink 任务提交请求并下发到 `cdp-flink-job-service`。

### 1.3 `cdp-tag-task-service`

1. 接收标签任务发布。
2. 实时标签模式下提交 Flink 模拟任务。
3. 查询任务状态并回写标签任务状态。

### 1.4 `cdp-flink-job-service`

1. 统一管理模拟 Flink 任务状态。
2. 提供任务状态查询与手工标记接口。
3. 生成模拟 ODS 数据并落库（MySQL 可用时）或内存兜底。

## 2. 关键调用链路

### 2.1 导入链路

`cdp-callchain-demo -> cdp-connector-control-service -> cdp-flink-job-service`

### 2.2 标签实时链路

`cdp-callchain-demo -> cdp-tag-task-service -> cdp-flink-job-service`

### 2.3 ODS 查询链路

`cdp-callchain-demo -> cdp-flink-job-service`

## 3. `sourceType` 到任务类型映射

| sourceType | 任务类型 |
| --- | --- |
| `MYSQL` / `MYSQL_CDC` | `IMPORT_MYSQL_TO_DORIS` |
| `KAFKA` | `IMPORT_KAFKA_TO_DORIS` |
| `ES` / `ELASTICSEARCH` | `IMPORT_ES_TO_DORIS` |
| `FILE` / `FS` / `FILE_SYSTEM` | `IMPORT_FILE_TO_DORIS` |

## 4. 实例化与高可用演示

关键服务支持双实例演示（2 个实例）：

1. `cdp-callchain-demo`
2. `cdp-connector-control-service`
3. `cdp-tag-task-service`
4. `cdp-flink-job-service`

目标是让 Nacos 中可见多实例注册，方便讲解高可用与故障隔离。
