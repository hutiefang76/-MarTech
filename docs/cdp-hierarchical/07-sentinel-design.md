# 07 - Sentinel Design (Rate Limit / Circuit Breaker / Degrade)

## 1. 设计目标

1. 对外暴露接口限流，防止演示入口被打爆。
2. 下游不稳定时快速熔断，避免级联故障。
3. 关键能力降级可用，保证演示链路不中断。

## 2. 接入范围

### 2.1 `cdp-callchain-demo`

重点资源：

1. `/demo/console/simulate/import*`
2. `/demo/console/simulate/tag-realtime`
3. `/demo/console/simulate/full-chain`
4. `/demo/console/ods/*`

### 2.2 `cdp-connector-control-service`

重点资源：

1. `/connector/control/import/deploy-k8s`

### 2.3 `cdp-tag-task-service`

重点资源：

1. `/tag/task/publish`
2. `/tag/task/{taskId}`

### 2.4 `cdp-flink-job-service`

重点资源：

1. `/flink/jobs/deploy-k8s`
2. `/flink/jobs/{jobId}`
3. `/flink/jobs/ods/*`

## 3. 限流规则建议

## 3.1 入口限流（QPS）

1. `simulate/import*`: 20 QPS
2. `simulate/tag-realtime`: 30 QPS
3. `simulate/full-chain`: 10 QPS
4. `ods/latest`: 50 QPS

### 3.2 参数限流（租户维度）

按 `tenantId` 做热点参数限流：

1. 单租户 `simulate/full-chain` 不超过 5 QPS。
2. 单租户 `ods/latest` 不超过 20 QPS。

## 4. 熔断规则建议

按慢调用比例和异常比例配置：

1. `connector-control -> flink-job`：RT > 800ms 且慢调用比例 > 40% 熔断 10s。
2. `tag-task -> flink-job`：异常比例 > 30% 熔断 10s。
3. `callchain -> connector-control`：异常比例 > 20% 熔断 5s。

## 5. 降级策略

### 5.1 读取类接口降级

1. `ods/latest` 下游失败时返回缓存或最近内存快照。
2. `job status` 下游失败时返回 `UNKNOWN` + 建议重试。

### 5.2 写入类接口降级

1. `import/deploy-k8s` 下游不可用时返回“已受理，稍后重试”。
2. 记录补偿任务，异步重放。

## 6. 统一降级返回体

建议统一结构：

1. `code`
2. `message`
3. `degradeType`
4. `traceId`
5. `retryAfterSeconds`

## 7. Sentinel 规则管理

1. 开发期可本地文件/内存规则。
2. 联调期可接 Nacos 动态规则。
3. 生产建议将 Flow/Degrade/System/ParamFlow 规则统一托管。

## 8. 与当前工程结合方式

1. 在 4 个关键服务加入 Sentinel starter。
2. 对上述资源添加 `@SentinelResource` 或网关规则。
3. 统一 blockHandler/fallback，输出结构化降级响应。
4. 把限流、熔断、降级事件写日志并接入 ELK。

## 9. 预期演示效果

1. 人工压测时能看到限流触发。
2. 关停下游服务时能看到熔断打开。
3. 读接口仍有降级返回，不至于全链路报错。
