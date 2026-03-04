# MarTech Core Systems (Clean Index)

本仓库目前聚焦 **MarTech 核心系统中 CDP 相关实现与演示链路**，目标是让后续 AI 或工程师可以按需加载内容，而不是一次性读取全部上下文。

## Read First

- 分层文档入口：`docs/cdp-hierarchical/00-index.md`
- 适合快速定位问题：先读 `00-index.md` 的“按需加载矩阵”

## Scope

1. CDP 业务边界与系统职责
2. CDP 关键微服务与调用链路
3. 四类数据源模拟（MySQL CDC / Kafka / ES / File）
4. ODS 模拟入库（MySQL 优先，内存兜底）
5. 可观测性（Nacos / SkyWalking / ELK / Dozzle）
6. 高可用演示（关键服务双实例）
7. 一致性策略与分布式事务边界

## Notes

1. 当前演示工程的业务逻辑全部为模拟逻辑，便于讲解微服务协同。
2. 默认存储采用 MySQL 模拟，避免 Doris/StarRocks 在低资源或非 Linux 环境的部署复杂度。
3. 生产方案可在此基础上替换为真实 Flink + Lakehouse + OLAP 组合。
