# MarTech 核心系统清单（系统级）

## 目标
明确 MarTech 核心系统与电商周边系统的依赖关系、数据流向和实现边界。

## 关键口径

1. CDP 采用“半一体”口径：内含数据接入与数据底座能力。  
2. MDM 独立于 CDP，作为企业主数据真相源。  
3. MA 聚焦营销自动化执行，不承载企业通用权限系统。  
4. 对外入口采用企业统一网关，MA 不单独建设独立外部网关。  
5. 本文按“依赖关系”描述，而非按时间顺序描述。

## 核心系统（按依赖关系）

| 序号 | 系统 | 核心职责 |
| --- | --- | --- |
| 1 | Tracking/TMS（Tag Management System） | 管理埋点与行为采集规则，采集 Web/App/小程序事件数据 |
| 2 | CRM（Customer Relationship Management） | 管理客户关系全生命周期（线索、商机、成交、客户信息） |
| 3 | SCRM（Social CRM） | 管理私域客户运营（企微、社群、导购、1v1互动） |
| 4 | Contact Center（Customer Service/Helpdesk） | 管理客服咨询、投诉、工单与满意度数据 |
| 5 | Loyalty（Loyalty Program） | 管理积分、等级、权益与复购激励机制 |
| 6 | MDM（Master Data Management） | 管理商品、门店、组织、渠道等主数据统一口径 |
| 7 | CDP（Customer Data Platform） | OneID、画像、标签、分群、激活与开放服务 |
| 8 | MA（Marketing Automation） | 旅程编排、规则决策、多渠道自动化触达 |
| 9 | Ad Platform（Advertising Platform） | 公域投放、人群拓展与回传 |
| 10 | CMS & DAM（Content Management System & Digital Asset Management） | 内容与素材统一管理与分发 |
| 11 | BI & Attribution（Business Intelligence & Attribution） | 经营分析、归因评估、预算优化 |

## 周边系统（电商）

| 序号 | 系统 | 核心职责 |
| --- | --- | --- |
| E1 | E-Commerce Platform（商城平台） | 商品浏览、购物车、结算、订单创建 |
| E2 | OMS（Order Management System） | 订单履约、拆单、状态流转、售后协同 |
| E3 | PIM（Product Information Management） | 商品主数据、类目、属性、上下架管理 |
| E4 | ERP（Enterprise Resource Planning） | 采购、财务、库存、供应链流程 |
| E5 | WMS（Warehouse Management System） | 仓储、拣配、发货、库存作业 |
| E6 | Payment Gateway（支付网关） | 支付下单、支付回调、退款协同 |
| E7 | POS（Point of Sale） | 线下交易与门店会员行为采集 |

## 系统依赖关系图（字符画）

```text
                            [Enterprise API Gateway]
                                      |
                 +--------------------+--------------------+
                 |                                         |
             [CDP API]                                 [Ecom Backend API]
                 |                                         |
[CRM/SCRM/ContactCenter/Loyalty] -> [MDM] -> [CDP] -> [MA] -> [BI & Attribution]
[Tracking/TMS] -------------------------------> |
[CMS & DAM] ----------------------------------> [MA]
[Ad Platform] <------------------------------- [CDP]
```

说明：
- 企业统一网关承接外部流量；MA 不单独建设独立外部网关。  
- 电商与客户系统共同向 MDM/CDP 提供关键数据。  
- CDP 向 MA/Ad 输出可运营人群，MA 回传触达结果给 CDP/BI。

---

## CDP 实现层详细规划

> 技术栈：`SpringCloud Alibaba + SpringBoot + MyBatis + MySQL + Redis + Kafka + Flink + Iceberg + Doris`。

### CDP 架构分层

1. 控制平面（微服务）：规则、任务、元数据、审计、开放 API。  
2. 计算平面（Flink）：实时任务与批量任务计算。  
3. 数据平面（实时数仓 + Iceberg Lakehouse + Doris + Redis）：统一存储与服务输出。

### CDP 微服务模块（建议）

#### 平台与治理域

| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-api-service` | CDP 对内/对外 API 编排与访问控制 | MySQL + Redis |
| `cdp-meta-service` | 事件模型、字段字典、标签字典、血缘元数据 | MySQL |
| `cdp-audit-service` | 操作审计、任务审计、数据访问审计 | MySQL + Doris |

#### 数据接入与身份域

| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-ingestion-service` | 接入埋点/API/文件数据，统一写入 Kafka | Kafka + MySQL |
| `cdp-connector-service` | 对接 CRM/OMS/ERP/POS/客服系统 | MySQL + Kafka |
| `cdp-identity-service` | OneID 匹配、合并/拆分、身份图谱维护 | MySQL + Redis |
| `cdp-profile-service` | 画像聚合、画像查询、画像服务输出 | Redis + Doris |

#### 标签与人群域

| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-tag-service` | 标签定义、生命周期、标签血缘管理 | MySQL |
| `cdp-tag-task-service` | 任务控制面（发布、版本、回滚、状态聚合） | MySQL |
| `cdp-segment-service` | 人群圈选、预估、分群执行入口 | Doris + Redis |
| `cdp-audience-service` | 人群包生成、导出、投放包管理 | Doris + MySQL |

#### 激活与开放域

| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `cdp-activation-service` | 向 MA/广告/CRM/SCRM 下发人群与标签，回收回执 | MySQL + Kafka |
| `cdp-openapi-service` | 标签/画像/分群命中查询服务 | Redis + Doris |

### CDP 关键能力到服务映射（你关心的 OneID/画像/标签/分群）

| 能力 | 主责服务 | 协同服务 |
| --- | --- | --- |
| OneID | `cdp-identity-service` | `cdp-ingestion-service`, `cdp-meta-service` |
| 画像 | `cdp-profile-service` | `cdp-identity-service`, `cdp-openapi-service` |
| 标签 | `cdp-tag-service` | `cdp-tag-task-service`, `cdp-openapi-service` |
| 分群 | `cdp-segment-service` | `cdp-audience-service`, `cdp-activation-service` |

### CDP 实时/批量计算口径（你要求的流仓 + 入湖 + 批写 Doris）

1. 全量实时数据先进入 Kafka。  
2. Flink 构建实时数仓层（实时清洗、关联、标签计算）。  
3. 实时结果持续写入 Iceberg Lakehouse（实时入湖）。  
4. 批量任务从 Iceberg Lakehouse 读取，做离线汇总与重算。  
5. 批量结果写入 Doris（分析与服务查询）。  
6. 热标签与高频画像写 Redis 提供低延迟服务。

### CDP 实现层关系图（系统抽象，不到 Topic）

```text
[Tracking/TMS + 业务系统] ---> [Kafka]
                                |
                                v
                       [Flink 实时数仓]
                                |
                                v
                    [Iceberg Lakehouse]
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

说明：
- 实时任务管理建议对接 `StreamPark`。  
- 批量任务调度建议对接 `DolphinScheduler`。  
- CDP 保留 `cdp-tag-task-service` 作为任务控制面，不重复造调度平台。

---

## MA 实现层详细规划

> 定位：MA 是触达执行中枢。权限与认证使用企业共享 IAM，不在 MA 内重复建设。

### MA 架构分层

1. 活动与旅程层：活动管理、旅程编排、节点状态机。  
2. 规则与决策层：规则引擎、决策引擎、频控去重。  
3. 通道执行层：短信/邮件/微信/Push/站内信路由与发送。  
4. 内容与实验层：模板管理、渲染、A/B 分流。  
5. 回执与反馈层：回执聚合、效果回传 CDP/BI。

### MA 微服务模块（建议）

| 服务名 | 核心职责 | 主要存储 |
| --- | --- | --- |
| `ma-campaign-service` | 活动管理（创建、发布、状态流转） | MySQL |
| `ma-journey-service` | 旅程编排（节点、分支、等待、退出） | MySQL |
| `ma-trigger-service` | 消费 CDP 标签变化/行为事件，触发旅程实例 | Kafka + Redis |
| `ma-rule-engine-service` | 规则执行（触发条件、频控、去重） | Redis + MySQL |
| `ma-decision-engine-service` | 决策执行（策略树、优先级、Offer 决策） | Redis + MySQL |
| `ma-segment-sync-service` | 同步 CDP 人群包和标签快照 | Redis + MySQL |
| `ma-template-service` | 模板、变量、版本管理 | MySQL |
| `ma-render-service` | 个性化渲染（变量替换、内容拼装） | Redis |
| `ma-channel-service` | 通道路由与发送编排（下挂各渠道适配器） | Redis + MySQL |
| `ma-scheduler-service` | 定时活动编排（可对接 DolphinScheduler） | MySQL |
| `ma-feedback-service` | 发送日志、回执聚合、效果回流 CDP/BI | Doris + MySQL + Kafka |

### MA 规则引擎 / 决策引擎技术建议（明确）

#### 规则引擎（Rule Engine）

适用：触发条件、频控、去重、黑白名单。  
技术建议：

1. `Drools`：复杂规则、规则版本化、前向推理场景。  
2. `Aviator/QLExpress`：轻量表达式规则，高性能计算判定。  
3. `Redis`：频控计数器与幂等键（如用户+活动+渠道）。

#### 决策引擎（Decision Engine）

适用：渠道选择、内容策略、Offer 优先级、冲突仲裁。  
技术建议：

1. 决策树 + 评分卡（服务内实现）用于高性能实时决策。  
2. `DMN/Flowable`（可选）用于可视化策略编排和审核。  
3. 特征输入来自 CDP 标签/画像与 Redis 热特征，输出“最佳下一动作”。

### MA 防用户疲劳（关键，放在调用链硬门控）

统一执行链：
`ma-trigger-service -> ma-rule-engine-service(疲劳门控) -> ma-decision-engine-service(渠道仲裁) -> ma-channel-service`

1. 全局频控门控：`ma-rule-engine-service` 用 Redis 计数，先判 `日/周总触达上限`，超限直接拦截。  
2. 渠道频控门控：短信/邮件/企微各自上限，避免单渠道轰炸。  
3. 静默期门控：夜间或敏感时段禁止营销触达。  
4. 冷却窗口门控：最近触达后未到冷却期（如 48 小时）不再触发。  
5. 冲突仲裁：`ma-decision-engine-service` 在多个活动并发命中时只保留最高优先级动作。  
6. 抑制名单：退订/投诉/黑名单在门控阶段直接拒绝发送。  
7. 动态降频：`ma-feedback-service` 回流退订/未读/投诉后，动态调低后续触达频率。

### MA 核心依赖关系图（字符画）

```text
[Enterprise API Gateway] ---> [ma-campaign-service / ma-journey-service]
[CDP: segment/tag/event] ---> [ma-trigger-service] ---> [ma-rule-engine-service]
                                                     \-> [ma-decision-engine-service]
                                                              |
                                                              v
                                                    [ma-channel-service]
                                                      /      |      \
                                              [SMS] [Email] [Wechat/Push]

[ma-template-service] --> [ma-render-service] --> [ma-channel-service]
[Delivery Receipts] --> [ma-feedback-service] --> [CDP] + [BI & Attribution]
```

### MA 与 CDP / BI 边界

1. CDP 负责识别与认知：OneID、画像、标签、分群。  
2. MA 负责执行与触达：何时触发、走哪个渠道、发什么内容。  
3. BI 负责评估与归因：触达效果、渠道贡献、预算优化。  
4. MA 回传回执与触达日志给 CDP/BI，形成闭环。

### MA 营销触达下游微服务清单（仅触达相关）

#### 通道触达类

1. `sms-service`：短信发送、模板审核、回执回传。  
2. `email-service`：邮件发送、模板管理、退信处理。  
3. `wecom-message-service`：企业微信消息发送与回执。  
4. `wechat-template-message-service`：微信模板消息发送。  
5. `push-service`：App Push 发送与点击回传。  
6. `inbox-message-service`：站内信投递与已读回传。

#### 营销权益触达类

1. `coupon-service`：优惠券发放、锁券、核销状态同步。  
2. `promotion-service`：促销活动命中校验与优惠计算。  
3. `points-service`：积分发放/扣减触发与状态回传。  
4. `loyalty-service`：会员权益触发、等级权益通知。

### MA 典型触达场景（按时效分类，10 个）

#### 强实时触达（秒级到 5 分钟）

1. 电销结束同意触达：电销人员通话结束且用户同意后，5 分钟内发送 `优惠券 + 短信 + 邮件 + 企业微信消息`。  
2. 支付失败挽回：支付失败后 5 分钟发送短信+站内信，附重试支付入口。  
3. 加购未下单：加购后 30 分钟未支付，触发短信或 Push，附限时券。  
4. 新客注册未首单：注册后 10 分钟发送欢迎券短信。

#### 弱实时触达（小时级到 T+1）

1. 浏览高意向未购：同品类浏览达到阈值后 2 小时触发邮件/站内信推荐。  
2. 新客注册二次提醒：首触达后 24 小时未转化再次提醒。  
3. 签收后复购：订单签收 T+1 发送关怀消息与复购券。  
4. 售后完成回流：售后完成后触发满意度邀请与定向补偿券触达。

#### 批量触达（日/周/月定时）

1. 老客召回：30 天未下单用户按日批圈选，先发短信和站内信，48 小时未回流再发企微+券。  
2. 券即将过期：按日批扫描到期券，到期前 24 小时发送短信+Push 催使用。  
3. 会员降级预警：按周批识别降级风险用户，降级前 7 天发送企微/邮件保级提醒。
