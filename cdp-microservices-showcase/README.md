# CDP Microservices Showcase

这个工程是 **CDP 微服务调用链路演示工程**，核心目标是可讲解、可观察、可复用。

## 定位

1. 业务逻辑全部是模拟逻辑（不接真实生产系统）。
2. 展示微服务边界、调用链、状态流转。
3. 演示 Nacos 注册/配置、SkyWalking 链路、ELK 日志检索。
4. 默认用 MySQL 模拟数仓侧存储，不强依赖 Doris/StarRocks。

## 核心模块

- `cdp-callchain-demo`（演示控制台 + 浏览器操作页）
- `cdp-connector-control-service`（模拟导入任务发布）
- `cdp-tag-task-service`（模拟实时标签任务发布）
- `cdp-flink-job-service`（模拟 Flink on K8s 任务状态）

## 演示页面

启动 `cdp-callchain-demo` 后访问：

- `http://localhost:19180/`

页面提供按钮：

- 查看拓扑
- 模拟导入链路
- 发布实时标签
- 一键全链路
- 查询标签任务
- 查询 Flink 状态
- 标记 Flink 状态（RUNNING/SUCCESS/FAILED）

## 中间件启动（项目内 sh/cmd）

目录：`middleware/docker-compose.yml`

- 核心中间件：MySQL / Redis / Nacos / Kafka
- 可观测组件（可选 profile）：SkyWalking / OTel Collector / ELK / Dozzle

Linux/macOS:

```bash
cd sh
bash start-middleware.sh
bash start-middleware.sh --with-observability
bash stop-middleware.sh
```

Windows:

```bat
cd cmd
start-middleware.cmd
start-middleware.cmd --with-observability
stop-middleware.cmd
```

## 数据持久化目录

所有容器数据都在工程根目录：

- `runtime-data/`

规则：

1. 重启 docker 后数据保留。
2. 删除 `runtime-data` 整个目录后，下次启动就是全新环境。

## Nacos 配置中心初始化

为了在 Nacos 配置中心直接看到配置，执行：

Linux/macOS:

```bash
cd sh
bash init-nacos-config.sh
```

Windows:

```bat
cd cmd
init-nacos-config.cmd
```

Nacos 控制台：

- `http://localhost:8848/nacos`

## 微服务高可用演示（每个服务 2 实例）

Linux/macOS:

```bash
cd sh
bash start-services-ha.sh
bash stop-services-ha.sh
```

Windows:

```bat
cd cmd
start-services-ha.cmd
stop-services-ha.cmd
```

默认会拉起 4 个关键服务各 2 实例（共 8 个进程），便于在 Nacos 看多实例注册。

## 可观测入口

- SkyWalking UI: `http://localhost:18080`
- Kibana: `http://localhost:5601`
- Dozzle（更简单容器日志界面）: `http://localhost:19999`

## 编译

```bash
mvn -q clean -DskipTests compile
```

## Doris / StarRocks / MySQL 选择建议

当前工程默认 MySQL 模拟，原因是本项目目标是讲解微服务链路，不是压测 OLAP。

建议：

1. Linux x86_64 机器资源足够时，再上 Doris 或 StarRocks。
2. 低内存机器（尤其开发机）优先 MySQL 模拟，稳定性更高。
3. macOS（含 Apple Silicon）优先用 MySQL 模拟，Doris/StarRocks 可通过远端 Linux 环境接入。

## 关键说明

1. 当前接口返回以演示和联调为主。
2. Flink 任务是模拟状态机，不是实际计算任务。
3. 本工程重点是“调用链、可观测、可讲解”的可行性。
