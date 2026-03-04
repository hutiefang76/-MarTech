package cn.javahome.frank.cdp.identity.service.impl;

import cn.javahome.frank.cdp.identity.entity.IdentityEntity;
import cn.javahome.frank.cdp.identity.feign.IdentityDemoFeignClient;
import cn.javahome.frank.cdp.identity.service.IdentityService;
import cn.javahome.frank.cdp.identity.util.IdentityUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class IdentityServiceImpl implements IdentityService {

    private final IdentityDemoFeignClient demoFeignClient;

    public IdentityServiceImpl(IdentityDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        IdentityEntity entity = new IdentityEntity(
                IdentityUtil.buildId("identity"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-identity-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
