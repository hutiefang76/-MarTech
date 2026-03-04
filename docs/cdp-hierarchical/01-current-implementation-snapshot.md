# 01 - Current Implementation Snapshot

## 1. 项目定位

当前实现是 **CDP 微服务演示工程**，目标是讲清楚微服务边界、调用链和可观测性，不追求生产级完整业务。

## 2. `cdp-callchain-demo` 的定位

`cdp-callchain-demo` 是额外的演示微服务，作用是：

1. 提供统一演示入口页面（按钮触发）。
2. 编排调用其他微服务（connector / tag-task / flink-job）。
3. 汇总返回结果，便于演示链路和状态流转。

它不是 CDP 核心业务域里必须存在的生产服务。

## 3. 当前核心演示服务

1. `cdp-callchain-demo`
2. `cdp-connector-control-service`
3. `cdp-tag-task-service`
4. `cdp-flink-job-service`

## 4. 当前可演示能力

1. 模拟数据导入任务发布（多 sourceType）。
2. 模拟标签实时任务发布。
3. 模拟 Flink 任务状态变更（RUNNING/SUCCESS/FAILED）。
4. 模拟 ODS 结果产出与查询。

## 5. 中间件与可观测能力

1. Nacos: 注册中心 + 配置中心（演示用）。
2. Kafka: 事件总线模拟。
3. MySQL: ODS 模拟落库。
4. SkyWalking: 调用链路展示。
5. ELK: 日志检索。
6. Dozzle: 更轻量的日志可视化。

## 6. 真实性边界

1. 当前业务逻辑是模拟，不包含真实上游业务系统连接。
2. 任务执行使用模拟状态机，不是实际 Flink 集群执行。
3. 但接口形态、链路编排、状态回传和运维视角与真实系统一致。
