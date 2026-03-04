# 99 - Progress Status (Completed vs Remaining)

## 1. 已完成内容

### 1.1 文档结构化改造

1. 重写主入口文档：`martech-core-systems.md`。
2. 新增分层目录：`docs/cdp-hierarchical/`。
3. 新增按需加载索引：`00-index.md`。

### 1.2 当前实现认知沉淀

1. 明确 `cdp-callchain-demo` 是演示编排服务，不是生产核心域服务。
2. 明确 4 个关键演示服务职责与调用链。
3. 明确四类数据源模拟能力（MySQL CDC / Kafka / ES / 文件）。
4. 明确 ODS 模拟数据落地策略（MySQL 优先，内存兜底）。
5. 整理可观测与 HA 运行手册（Nacos/SkyWalking/ELK/Dozzle）。
6. 补充一致性与分布式事务边界说明。
7. 新增 Sentinel 限流/熔断/降级设计。
8. 整理演示接口清单。

### 1.3 已新增文档清单

1. `docs/cdp-hierarchical/00-index.md`
2. `docs/cdp-hierarchical/01-current-implementation-snapshot.md`
3. `docs/cdp-hierarchical/02-service-boundaries-and-callchain.md`
4. `docs/cdp-hierarchical/03-data-source-and-ods-simulation.md`
5. `docs/cdp-hierarchical/04-observability-ha-runbook.md`
6. `docs/cdp-hierarchical/05-consistency-and-resilience-guide.md`
7. `docs/cdp-hierarchical/06-api-reference.md`
8. `docs/cdp-hierarchical/07-sentinel-design.md`
9. `docs/cdp-hierarchical/99-progress-status.md`（本文件）

## 2. 还需要做的内容

### 2.1 代码层（如果继续推进）

1. 将 Sentinel 设计落地为实际代码配置与规则加载。
2. 完成 Nacos 动态规则推送与变更演示。
3. 为降级响应统一返回体增加实际实现。
4. 增加压测脚本验证限流/熔断阈值。

### 2.2 演示层（可选增强）

1. 演示页增加“Sentinel 规则实时开关/阈值调节”面板。
2. 演示页增加“触发限流/熔断”的可视化提示。
3. 增加链路回放按钮（按 traceId 回放请求轨迹）。

### 2.3 运维层（可选增强）

1. 增加一键健康检查脚本（端口+接口+依赖）。
2. 增加日志看板预置（Kibana dashboard 导出文件）。
3. 增加 SkyWalking 预置拓扑看板说明。

## 3. 当前建议状态

当前文档已经可用于后续 AI 快速理解与按需加载。
如果要继续进入“可演示可验证”的阶段，优先做 2.1 第 1-3 项。
