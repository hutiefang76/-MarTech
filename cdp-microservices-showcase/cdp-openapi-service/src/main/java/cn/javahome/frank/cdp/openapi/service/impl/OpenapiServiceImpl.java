package cn.javahome.frank.cdp.openapi.service.impl;

import cn.javahome.frank.cdp.openapi.entity.OpenapiEntity;
import cn.javahome.frank.cdp.openapi.feign.OpenapiDemoFeignClient;
import cn.javahome.frank.cdp.openapi.service.OpenapiService;
import cn.javahome.frank.cdp.openapi.util.OpenapiUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class OpenapiServiceImpl implements OpenapiService {

    private final OpenapiDemoFeignClient demoFeignClient;

    public OpenapiServiceImpl(OpenapiDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        OpenapiEntity entity = new OpenapiEntity(
                OpenapiUtil.buildId("openapi"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-openapi-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
