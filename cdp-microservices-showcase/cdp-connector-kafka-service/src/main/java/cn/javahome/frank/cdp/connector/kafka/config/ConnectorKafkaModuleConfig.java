package cn.javahome.frank.cdp.connector.kafka.config;

import org.springframework.context.annotation.Configuration;

/**
 * 简化配置层：
 * 1. 该服务预留 Nacos 注册与配置中心参数；
 * 2. 该服务预留 MyBatis/MySQL/Redis/Kafka 接入能力。
 */
@Configuration
public class ConnectorKafkaModuleConfig {
}
