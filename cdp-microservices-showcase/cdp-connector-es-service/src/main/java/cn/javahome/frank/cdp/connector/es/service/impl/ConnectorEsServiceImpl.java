package cn.javahome.frank.cdp.connector.es.service.impl;

import cn.javahome.frank.cdp.connector.es.entity.ConnectorEsEntity;
import cn.javahome.frank.cdp.connector.es.feign.ConnectorEsDemoFeignClient;
import cn.javahome.frank.cdp.connector.es.service.ConnectorEsService;
import cn.javahome.frank.cdp.connector.es.util.ConnectorEsUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorEsServiceImpl implements ConnectorEsService {

    private final ConnectorEsDemoFeignClient demoFeignClient;

    public ConnectorEsServiceImpl(ConnectorEsDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorEsEntity entity = new ConnectorEsEntity(
                ConnectorEsUtil.buildId("connector-es"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-es-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
