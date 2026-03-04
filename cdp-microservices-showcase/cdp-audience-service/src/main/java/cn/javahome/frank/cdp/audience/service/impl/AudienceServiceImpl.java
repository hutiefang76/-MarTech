package cn.javahome.frank.cdp.audience.service.impl;

import cn.javahome.frank.cdp.audience.entity.AudienceEntity;
import cn.javahome.frank.cdp.audience.feign.AudienceDemoFeignClient;
import cn.javahome.frank.cdp.audience.service.AudienceService;
import cn.javahome.frank.cdp.audience.util.AudienceUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class AudienceServiceImpl implements AudienceService {

    private final AudienceDemoFeignClient demoFeignClient;

    public AudienceServiceImpl(AudienceDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        AudienceEntity entity = new AudienceEntity(
                AudienceUtil.buildId("audience"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-audience-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
