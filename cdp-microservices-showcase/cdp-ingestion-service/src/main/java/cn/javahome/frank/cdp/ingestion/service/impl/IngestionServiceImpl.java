package cn.javahome.frank.cdp.ingestion.service.impl;

import cn.javahome.frank.cdp.ingestion.entity.IngestionEntity;
import cn.javahome.frank.cdp.ingestion.feign.IngestionDemoFeignClient;
import cn.javahome.frank.cdp.ingestion.service.IngestionService;
import cn.javahome.frank.cdp.ingestion.util.IngestionUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class IngestionServiceImpl implements IngestionService {

    private final IngestionDemoFeignClient demoFeignClient;

    public IngestionServiceImpl(IngestionDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        IngestionEntity entity = new IngestionEntity(
                IngestionUtil.buildId("ingestion"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-ingestion-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
