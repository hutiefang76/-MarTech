# 04 - Observability And HA Runbook

## 1. 目录与持久化策略

所有容器数据放在工程根目录 `runtime-data/`。

规则：

1. 重启 Docker 可复用历史数据。
2. 删除 `runtime-data/` 后获得全新环境。

## 2. 中间件启动

### 2.1 核心中间件

- MySQL
- Redis
- Nacos
- Kafka

### 2.2 可观测组件（可选）

- SkyWalking
- OTel Collector
- Elasticsearch
- Logstash
- Kibana
- Dozzle

## 3. Nacos 能看到什么

### 3.1 实例

双实例启动后，可在 Nacos 服务列表看到每个服务 2 个实例。

### 3.2 配置

通过初始化脚本可把演示配置发布到 Nacos 配置中心。

## 4. SkyWalking 能看到什么

1. 服务调用拓扑。
2. 接口级链路耗时。
3. 失败请求链路。

推荐演示流量：

1. 调用 `simulate/import/*`
2. 调用 `simulate/tag-realtime`
3. 查询 `ods/*`

## 5. ELK/Dozzle 能看到什么

1. Spring Boot 服务日志。
2. 任务触发与状态变更日志。
3. 调用失败与超时日志。

说明：

1. Kibana 功能最全。
2. Dozzle 更轻量，更适合“傻瓜式快速看日志”。

## 6. 双实例启动说明

关键服务支持 2 实例启动：

1. `cdp-callchain-demo`
2. `cdp-connector-control-service`
3. `cdp-tag-task-service`
4. `cdp-flink-job-service`

目的是演示：

1. 注册中心中的多实例可见性。
2. 微服务高可用基础形态。
3. 故障时的实例隔离。

## 7. 常见问题

### 7.1 Docker daemon 未启动

症状：连接 `dockerDesktopLinuxEngine` 失败。

处理：先启动 Docker Desktop 再执行脚本。

### 7.2 端口冲突

症状：服务启动失败或接口超时。

处理：释放占用端口后重启。

### 7.3 启动后 Nacos 无实例

处理检查：

1. Nacos 是否可访问。
2. 服务是否实际启动成功。
3. `server.port` 与实例 ID 是否正确。
