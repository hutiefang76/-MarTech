# MarTech 核心系统方案

## 第1章 MarTech技术介绍、作用与发展

### 1.1 MarTech技术介绍
MarTech 是 Marketing Technology 的简称，指用技术和数据驱动营销全流程的一类系统能力。  
它覆盖从数据采集、用户识别、标签分群、策略决策、自动化触达到效果归因的完整闭环。  
在电商场景中，MarTech 的核心价值是把分散的用户行为和交易数据转化为可执行的运营动作。

### 1.2 MarTech主要作用
1. 提升获客效率：通过人群识别和渠道优化提升投放效率。  
2. 提升转化效率：通过实时触达和个性化内容提升下单转化。  
3. 提升复购效率：通过会员运营与生命周期运营提高复购率。  
4. 提升经营效率：通过归因分析和效果看板支持预算优化与策略复盘。

### 1.3 MarTech发展
1. 阶段一：工具化阶段，以单点营销工具为主。  
2. 阶段二：平台化阶段，以 CDP 和 MA 为中心形成营销平台。  
3. 阶段三：智能化阶段，以实时数据和策略引擎驱动自动决策。  
4. 阶段四：一体化阶段，打通营销、交易、服务数据形成经营闭环。

### 1.4 本文目标
按依赖关系梳理 MarTech 核心系统和电商周边系统，输出可落地的 CDP 与 MA 微服务规划，以及典型场景的执行链路与关键技术点。

## 第2章 系统总览

### 3.1 核心系统
| 序号 | 系统中文名 | 系统英文名 | 全称 | 核心职责 |
| --- | --- | --- | --- | --- |
| 1 | 埋点与采集管理 | Tracking/TMS | Tracking and Tag Management System | 管理埋点与行为采集规则，采集 Web/App/小程序事件数据 |
| 2 | 客户关系管理 | CRM | Customer Relationship Management | 管理客户关系全生命周期，包含线索、商机、成交、客户信息 |
| 3 | 私域客户管理 | SCRM | Social Customer Relationship Management | 管理企微、社群、导购与 1v1 私域互动 |
| 4 | 客服与工单系统 | Contact Center | Contact Center | 管理咨询、投诉、工单与满意度数据 |
| 5 | 会员忠诚度系统 | Loyalty Program | Loyalty Program | 管理积分、等级、权益与复购激励 |
| 6 | 主数据管理 | MDM | Master Data Management | 管理商品、门店、组织、渠道等主数据口径 |
| 7 | 客户数据平台 | CDP | Customer Data Platform | 管理 OneID、画像、标签、分群、激活与开放服务 |
| 8 | 营销自动化 | MA | Marketing Automation | 管理旅程编排、规则决策、多渠道自动化触达 |
| 9 | 广告投放平台 | Ad Platform | Advertising Platform | 管理公域投放、人群拓展与效果回传 |
| 10 | 内容与素材平台 | CMS & DAM | Content Management System and Digital Asset Management | 管理内容与素材资产的统一分发 |
| 11 | 分析与归因平台 | BI & Attribution | Business Intelligence and Attribution | 提供经营分析、归因评估、预算优化 |

### 3.2 周边系统
| 序号 | 系统中文名 | 系统英文名 | 全称 | 核心职责 |
| --- | --- | --- | --- | --- |
| E1 | 商城平台 | E-Commerce Platform | E-Commerce Platform | 商品浏览、购物车、结算、订单创建 |
| E2 | 订单管理系统 | OMS | Order Management System | 订单履约、拆单、状态流转、售后协同 |
| E3 | 商品信息管理 | PIM | Product Information Management | 商品主数据、类目、属性、上下架管理 |
| E4 | 企业资源计划 | ERP | Enterprise Resource Planning | 采购、财务、库存、供应链流程 |
| E5 | 仓储管理系统 | WMS | Warehouse Management System | 仓储、拣配、发货、库存作业 |
| E6 | 支付网关 | Payment Gateway | Payment Gateway | 支付下单、回调、退款协同 |
| E7 | 门店销售系统 | POS | Point of Sale | 线下交易与门店会员行为采集 |

### 3.3 系统依赖关系图
```text
                            [Enterprise API Gateway]
                                      |
                 +--------------------+--------------------+
                 |                                         |
              [CDP API]                               [Ecom Backend API]
                 |                                         |
[CRM/SCRM/ContactCenter/Loyalty] -> [MDM] -> [CDP] -> [MA] -> [BI & Attribution]
[Tracking/TMS] -------------------------------> |
[CMS & DAM] ----------------------------------> [MA]
[Ad Platform] <------------------------------- [CDP]
```

## 第3章 技术栈与作用
| 技术 | 作用 |
| --- | --- |
| Spring Cloud Alibaba | 提供服务治理能力，覆盖注册发现、配置管理、限流熔断、分布式事务协同 |
| Spring Boot | 提供微服务应用快速开发与运行框架 |
| MyBatis | 提供数据库访问映射与 SQL 管理能力 |
| MySQL | 存储交易型数据、规则配置、任务配置、业务主数据 |
| Redis | 提供高频状态读写、幂等、频控、缓存、热数据服务 |
| Kafka | 提供事件总线和异步解耦能力 |
| Flink | 提供流式计算、实时规则计算、实时指标计算能力 |
| Iceberg | 提供数据湖表格式能力，支持实时入湖、快照、回放、重算 |
| Doris | 提供高并发明细查询、聚合查询、看板查询能力 |
| DolphinScheduler | 提供离线和批量任务编排与调度能力 |
| StreamPark | 提供 Flink 作业发布、运维和实时任务管理能力 |
| Seata | 提供跨服务分布式事务协调能力，适用于权益发放等跨库一致性场景 |

## 第4章 CDP 实现层详细规划

### 5.1 CDP 主要作用定位
CDP 的核心定位是把多源客户数据统一成可运营资产，向上承接数据接入与身份统一，向下服务 MA、广告、CRM、BI。  
它不等于单纯的数据仓库，而是面向营销运营的客户数据中台。

### 5.2 CDP 微服务模块
#### 平台与治理域
| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-api-service` | CDP 对内对外 API 编排与访问控制 | MySQL + Redis |
| `cdp-meta-service` | 事件模型、字段字典、标签字典、血缘元数据管理 | MySQL |
| `cdp-audit-service` | 操作审计、任务审计、数据访问审计 | MySQL + Doris |

#### 数据接入与身份域
| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-ingestion-service` | 接入埋点、API、文件数据并写入 Kafka | Kafka + MySQL |
| `cdp-connector-service` | 对接 CRM、OMS、ERP、POS、客服系统 | MySQL + Kafka |
| `cdp-identity-service` | OneID 匹配、合并、拆分、身份图谱维护 | MySQL + Redis |
| `cdp-profile-service` | 画像聚合、画像查询、画像服务输出 | Redis + Doris |

#### 标签与人群域
| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-tag-service` | 标签定义、生命周期、标签血缘管理 | MySQL |
| `cdp-tag-task-service` | 任务控制面，负责发布、版本、回滚、状态聚合 | MySQL |
| `cdp-segment-service` | 人群圈选、预估、分群执行入口 | Doris + Redis |
| `cdp-audience-service` | 人群包生成、导出、投放包管理 | Doris + MySQL |

#### 激活与开放域
| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-activation-service` | 向 MA、广告、CRM、SCRM 下发人群与标签并回收回执 | MySQL + Kafka |
| `cdp-openapi-service` | 标签、画像、分群命中查询服务 | Redis + Doris |

### 5.3 CDP 关键能力映射
| 能力 | 主责服务 | 协同服务 |
| --- | --- | --- |
| OneID | `cdp-identity-service` | `cdp-ingestion-service`, `cdp-meta-service` |
| 画像 | `cdp-profile-service` | `cdp-identity-service`, `cdp-openapi-service` |
| 标签 | `cdp-tag-service` | `cdp-tag-task-service`, `cdp-openapi-service` |
| 分群 | `cdp-segment-service` | `cdp-audience-service`, `cdp-activation-service` |

### 5.4 CDP 实时与批量计算口径
1. 全量实时数据先进入 Kafka。  
2. Flink 构建实时数仓层，完成实时清洗、关联、标签计算。  
3. 实时结果持续写入 Iceberg。  
4. 批量任务从 Iceberg 读取，做离线汇总和重算。  
5. 批量结果写入 Doris 供分析与服务查询。  
6. 热标签与高频画像写 Redis 提供低延迟服务。

### 5.5 CDP 实现关系图
```text
[Tracking/TMS + 业务系统] ---> [Kafka]
                                |
                                v
                       [Flink 实时数仓]
                                |
                                v
                           [Iceberg]
                                |
                    +-----------+-----------+
                    |                       |
                    v                       v
             [Flink 批量任务]          [实时标签增量]
                    |                       |
                    v                       v
                 [Doris]                 [Redis]
                    |                       |
                    +-----------+-----------+
                                |
                                v
                      [CDP 微服务查询与下游服务]
                                |
                                v
                       [MA / Ad / CRM / BI]
```

## 第5章 CDP 业务场景
1. 跨渠道身份统一，打通同一用户在 App、H5、门店的行为。  
2. 实时高意向标签识别，支持分钟级触发营销。  
3. 会员生命周期分层，支持拉新、促活、复购分策略运营。  
4. 高价值人群识别，向 MA 与广告平台输出高价值包。  
5. 流失预警人群识别，支持召回策略触发。  
6. 大促实时分群，按实时行为动态调整触达策略。  
7. 负反馈用户抑制，减少骚扰触达。  
8. 交易与触达效果回流，形成闭环优化。  

## 第6章 MA 实现层详细规划

### 7.1 MA 微服务模块
| 服务名 | 下属模块 | 核心职责 | 主要存储 |
| --- | --- | --- | --- |
| `ma-campaign-service` | `campaign-draft`, `campaign-publish` | 活动管理，包含创建、发布、状态流转 | MySQL |
| `ma-journey-service` | `journey-designer`, `journey-instance` | 旅程编排，包含节点、分支、等待、退出 | MySQL |
| `ma-trigger-service` | `event-trigger`, `delay-trigger` | 消费 CDP 事件与标签变化，触发旅程实例 | Kafka + Redis |
| `ma-rule-engine-service` | `rule-parser`, `rule-executor` | 规则执行，覆盖触发条件、频控、去重 | Redis + MySQL |
| `ma-risk-control-service` | `fatigue-control`, `quiet-hours`, `suppression-list` | 触达风控门控，覆盖疲劳度、静默期、黑白名单 | Redis + MySQL |
| `ma-decision-engine-service` | `channel-ranking`, `offer-selector`, `conflict-arbiter` | 决策执行，覆盖渠道优选、权益优选、冲突仲裁 | Redis + MySQL |
| `ma-segment-sync-service` | `full-sync`, `delta-sync` | 同步 CDP 人群包和标签快照 | Redis + MySQL |
| `ma-template-service` | `template-repo`, `variable-dict`, `template-version` | 模板、变量、版本管理 | MySQL |
| `ma-render-service` | `render-core`, `personalization-render` | 个性化渲染，完成变量替换与内容拼装 | Redis |
| `ma-channel-service` | `sms-adapter`, `email-adapter`, `wecom-adapter`, `wechat-adapter`, `push-adapter`, `inbox-adapter` | 通道路由与发送编排 | Redis + MySQL |
| `ma-scheduler-service` | `cron-dispatcher`, `dolphinscheduler-bridge` | 定时任务编排，对接 DolphinScheduler | MySQL |
| `ma-feedback-service` | `receipt-consumer`, `delivery-tracker`, `retry-dlq-handler` | 回执聚合、重试处理、效果回流 | Doris + MySQL + Kafka |

### 7.2 MA 核心调用链
`ma-trigger-service -> ma-rule-engine-service -> ma-risk-control-service -> ma-decision-engine-service -> ma-channel-service -> ma-feedback-service`

### 7.3 MA 规则引擎与决策引擎技术
#### 规则引擎
1. `Drools` 适合复杂规则和规则版本化。  
2. `Aviator/QLExpress` 适合轻量表达式与高性能判定。  
3. `Redis` 承载频控计数器和幂等键。

#### 决策引擎
1. 决策树与评分卡用于实时动作选择。  
2. `DMN/Flowable` 可用于可视化策略编排。  
3. 输入特征来自 CDP 标签画像与 Redis 热特征。

### 7.4 MA 营销触达下游微服务
#### 通道触达类
1. `sms-service`  
2. `email-service`  
3. `wecom-message-service`  
4. `wechat-template-message-service`  
5. `push-service`  
6. `inbox-message-service`

#### 营销权益触达类
1. `coupon-service`  
2. `promotion-service`  
3. `points-service`  
4. `loyalty-service`

## 第7章 MA 典型触达场景

### 8.1 强实时触达
1. 电销结束同意触达：通话结束且用户同意后，5 分钟内发送优惠券、短信、邮件、企业微信消息。  
2. 支付失败挽回：支付失败后 5 分钟发送短信和站内信，附重试入口。  
3. 加购未下单：加购后 30 分钟未支付，触发短信或 Push。  
4. 新客注册未首单：注册后 10 分钟发送欢迎券短信。

### 8.2 弱实时触达
1. 浏览高意向未购：达到浏览阈值后 2 小时触发邮件或站内信。  
2. 签收后复购：签收 T+1 发送关怀消息和复购券。  
3. 售后完成回流：售后完成后触发满意度邀请和补偿券触达。

### 8.3 批量触达
1. 老客召回：30 天未下单用户按日批圈选，先发短信和站内信，48 小时未回流再发企微和优惠券。  
2. 券即将过期：按日批扫描到期券，到期前 24 小时发送短信和 Push。  
3. 会员降级预警：按周批识别降级风险用户，降级前 7 天发送企微和邮件提醒。

## 第8章 场景1技术细节

### 技术点1 Kafka 一致性语义
`EOS` 表示精确一次语义。  
工程落地建议：
1. 纯 Kafka 链路可用事务实现 EOS。  
2. 跨数据库和外部系统采用业务精确一次。  
3. 推荐使用幂等键、唯一约束、手动提交 offset、Outbox、DLQ。

场景1语义建议：
1. 优惠券发放使用精确一次微服务幂等。  
2. 短信发送使用至多一次。  
3. 邮件和企微使用至少一次加去重。

Spring Cloud Alibaba 与 Spring Kafka 消费要点：
1. `enable-auto-commit=false`，使用手动 ack。  
2. 先幂等校验再执行业务再提交 offset。  
3. 使用 Outbox 避免数据库成功但消息未发。  
4. 失败消息进入重试与 DLQ。

### 技术点2 分布式事务 Seata
场景：场景1中的权益包发放，包含优惠券和积分。  
参与服务：
1. `ma-trigger-service`
2. `ma-decision-engine-service`
3. `ma-benefit-orchestrator-service`
4. `coupon-service`
5. `points-service`
6. `activity-service`

事务链路：
1. `ma-benefit-orchestrator-service` 开启 `@GlobalTransactional`。  
2. Try 阶段冻结名额、券资源、积分额度。  
3. Confirm 阶段正式发券、发积分、扣减名额。  
4. Cancel 阶段统一解冻回滚。

边界：
1. Seata 负责跨服务资源一致性。  
2. 券发放仍需 `bizId` 幂等键。  
3. 短信、邮件、企微不纳入全局事务。

## 第9章 场景10技术细节

### 执行链路
1. DolphinScheduler 周批触发风险识别任务，读取 Iceberg 与 Doris 明细。  
2. 产出风险名单写入 Kafka。  
3. `ma-segment-sync-service` 入库并生成 D-7 触达计划。  
4. `ma-scheduler-service` 触发规则和风控校验。  
5. `ma-channel-service` 调用企微和邮件服务发送。  
6. 回执事件进入 Kafka，由 `ma-feedback-service` 更新 Redis 并落 Doris 看板。

### 技术点1 Redis 实时状态
Key：`ma:member:warn:user:{campaignId}:{userId}`。  
字段：
1. `total_sent`
2. `total_read`
3. `wecom_sent`, `wecom_read`
4. `email_sent`, `email_read`
5. `send_fail`
6. `last_send_ts`, `last_read_ts`
7. `next_send_ts`
8. `status`

### 技术点2 Doris 活动实时看板
1. 明细层表：`dwd_ma_member_warn_event`。  
2. 看板层表：`ads_ma_member_warn_dashboard`。  
3. 核心指标：`target_total`, `sent_total`, `read_total`, `fail_total`, `wecom_sent/read`, `email_sent/read`。  
4. 更新方式：Flink 持续写明细，分钟级聚合刷新看板。

### 技术点3 Redis 与 Doris 一致性
1. 回执消费使用 `msgId` 幂等。  
2. Redis 计数使用 Lua 原子更新。  
3. Redis 用于实时查询，Doris 用于分析与审计。  
4. 每日执行 Redis 与 Doris 对账，偏差按事件回放修正。  
5. Redis 设置 TTL，长期数据保留在 Doris。
