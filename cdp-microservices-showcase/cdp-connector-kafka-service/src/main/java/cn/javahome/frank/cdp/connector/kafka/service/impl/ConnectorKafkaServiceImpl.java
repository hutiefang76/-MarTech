package cn.javahome.frank.cdp.connector.kafka.service.impl;

import cn.javahome.frank.cdp.connector.kafka.entity.ConnectorKafkaEntity;
import cn.javahome.frank.cdp.connector.kafka.feign.ConnectorKafkaDemoFeignClient;
import cn.javahome.frank.cdp.connector.kafka.service.ConnectorKafkaService;
import cn.javahome.frank.cdp.connector.kafka.util.ConnectorKafkaUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorKafkaServiceImpl implements ConnectorKafkaService {

    private final ConnectorKafkaDemoFeignClient demoFeignClient;

    public ConnectorKafkaServiceImpl(ConnectorKafkaDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorKafkaEntity entity = new ConnectorKafkaEntity(
                ConnectorKafkaUtil.buildId("connector-kafka"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-kafka-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
