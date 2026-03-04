package cn.javahome.frank.cdp.connector.mysql.cdc.service.impl;

import cn.javahome.frank.cdp.connector.mysql.cdc.entity.ConnectorMysqlCdcEntity;
import cn.javahome.frank.cdp.connector.mysql.cdc.feign.ConnectorMysqlCdcDemoFeignClient;
import cn.javahome.frank.cdp.connector.mysql.cdc.service.ConnectorMysqlCdcService;
import cn.javahome.frank.cdp.connector.mysql.cdc.util.ConnectorMysqlCdcUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorMysqlCdcServiceImpl implements ConnectorMysqlCdcService {

    private final ConnectorMysqlCdcDemoFeignClient demoFeignClient;

    public ConnectorMysqlCdcServiceImpl(ConnectorMysqlCdcDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorMysqlCdcEntity entity = new ConnectorMysqlCdcEntity(
                ConnectorMysqlCdcUtil.buildId("connector-mysql-cdc"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-mysql-cdc-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
