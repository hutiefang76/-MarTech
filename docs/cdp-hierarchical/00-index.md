# CDP Hierarchical Context - Index

本文档集用于后续 AI 按需加载上下文。

## 1. 文档分层

- L0: 快速导览（当前文件）
- L1: 主题说明（01-05）
- L2: 接口细节（06）

## 2. 按需加载矩阵

| 你要回答的问题 | 最小加载文档 |
| --- | --- |
| `cdp-callchain-demo` 是什么 | `01-current-implementation-snapshot.md` |
| 当前微服务有哪些，怎么协同 | `02-service-boundaries-and-callchain.md` |
| MySQL CDC / Kafka / ES / 文件是怎么模拟的 | `03-data-source-and-ods-simulation.md` |
| ODS 数据在哪里，怎么查到结果 | `03-data-source-and-ods-simulation.md` + `06-api-reference.md` |
| 为什么 CDP 通常不做分布式事务 | `05-consistency-and-resilience-guide.md` |
| 限流 / 熔断 / 降级有哪些场景 | `05-consistency-and-resilience-guide.md` |
| Sentinel 怎么设计到接口层 | `07-sentinel-design.md` |
| 如何启动并观察 Nacos/SkyWalking/ELK | `04-observability-ha-runbook.md` |

## 3. 阅读顺序建议

1. 先读 `01-current-implementation-snapshot.md` 建立全局视图。
2. 再读 `02-service-boundaries-and-callchain.md` 理解职责与链路。
3. 如果关注“数据结果可见”，读 `03-data-source-and-ods-simulation.md`。
4. 如果关注“稳定性与治理”，读 `05-consistency-and-resilience-guide.md`。
5. 查接口时只读 `06-api-reference.md`。

## 4. 当前结论（便于快速答复）

1. `cdp-callchain-demo` 是 **额外的演示微服务**，不是生产必需核心域服务。
2. 当前数据源任务全部是模拟逻辑，但流程形态与真实架构一致。
3. CDP 数据面通常不用强一致分布式事务，主要采用最终一致。
4. 限流、熔断、降级在 CDP 中是刚需能力，尤其体现在入口和下游调用。
