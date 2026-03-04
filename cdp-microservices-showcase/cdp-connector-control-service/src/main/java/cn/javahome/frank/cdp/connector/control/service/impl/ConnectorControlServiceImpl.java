package cn.javahome.frank.cdp.connector.control.service.impl;

import cn.javahome.frank.cdp.connector.control.entity.ConnectorControlEntity;
import cn.javahome.frank.cdp.connector.control.feign.ConnectorControlDemoFeignClient;
import cn.javahome.frank.cdp.connector.control.service.ConnectorControlService;
import cn.javahome.frank.cdp.connector.control.util.ConnectorControlUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorControlServiceImpl implements ConnectorControlService {

    private final ConnectorControlDemoFeignClient demoFeignClient;

    public ConnectorControlServiceImpl(ConnectorControlDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorControlEntity entity = new ConnectorControlEntity(
                ConnectorControlUtil.buildId("connector-control"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-control-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
