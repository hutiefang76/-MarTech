package cn.javahome.frank.cdp.connector.fs.service.impl;

import cn.javahome.frank.cdp.connector.fs.entity.ConnectorFsEntity;
import cn.javahome.frank.cdp.connector.fs.feign.ConnectorFsDemoFeignClient;
import cn.javahome.frank.cdp.connector.fs.service.ConnectorFsService;
import cn.javahome.frank.cdp.connector.fs.util.ConnectorFsUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorFsServiceImpl implements ConnectorFsService {

    private final ConnectorFsDemoFeignClient demoFeignClient;

    public ConnectorFsServiceImpl(ConnectorFsDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorFsEntity entity = new ConnectorFsEntity(
                ConnectorFsUtil.buildId("connector-fs"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-fs-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
