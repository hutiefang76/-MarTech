package cn.javahome.frank.cdp.meta.service.impl;

import cn.javahome.frank.cdp.meta.entity.MetaEntity;
import cn.javahome.frank.cdp.meta.feign.MetaDemoFeignClient;
import cn.javahome.frank.cdp.meta.service.MetaService;
import cn.javahome.frank.cdp.meta.util.MetaUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class MetaServiceImpl implements MetaService {

    private final MetaDemoFeignClient demoFeignClient;

    public MetaServiceImpl(MetaDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        MetaEntity entity = new MetaEntity(
                MetaUtil.buildId("meta"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-meta-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
