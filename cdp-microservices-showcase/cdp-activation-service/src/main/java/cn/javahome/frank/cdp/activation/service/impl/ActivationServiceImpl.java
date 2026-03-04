package cn.javahome.frank.cdp.activation.service.impl;

import cn.javahome.frank.cdp.activation.entity.ActivationEntity;
import cn.javahome.frank.cdp.activation.feign.ActivationDemoFeignClient;
import cn.javahome.frank.cdp.activation.service.ActivationService;
import cn.javahome.frank.cdp.activation.util.ActivationUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ActivationServiceImpl implements ActivationService {

    private final ActivationDemoFeignClient demoFeignClient;

    public ActivationServiceImpl(ActivationDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ActivationEntity entity = new ActivationEntity(
                ActivationUtil.buildId("activation"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-activation-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
