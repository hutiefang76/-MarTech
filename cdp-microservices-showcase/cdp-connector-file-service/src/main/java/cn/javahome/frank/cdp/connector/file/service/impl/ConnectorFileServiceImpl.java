package cn.javahome.frank.cdp.connector.file.service.impl;

import cn.javahome.frank.cdp.connector.file.entity.ConnectorFileEntity;
import cn.javahome.frank.cdp.connector.file.feign.ConnectorFileDemoFeignClient;
import cn.javahome.frank.cdp.connector.file.service.ConnectorFileService;
import cn.javahome.frank.cdp.connector.file.util.ConnectorFileUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ConnectorFileServiceImpl implements ConnectorFileService {

    private final ConnectorFileDemoFeignClient demoFeignClient;

    public ConnectorFileServiceImpl(ConnectorFileDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ConnectorFileEntity entity = new ConnectorFileEntity(
                ConnectorFileUtil.buildId("connector-file"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-connector-file-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
