package cn.javahome.frank.cdp.segment.service.impl;

import cn.javahome.frank.cdp.segment.entity.SegmentEntity;
import cn.javahome.frank.cdp.segment.feign.SegmentDemoFeignClient;
import cn.javahome.frank.cdp.segment.service.SegmentService;
import cn.javahome.frank.cdp.segment.util.SegmentUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class SegmentServiceImpl implements SegmentService {

    private final SegmentDemoFeignClient demoFeignClient;

    public SegmentServiceImpl(SegmentDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        SegmentEntity entity = new SegmentEntity(
                SegmentUtil.buildId("segment"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-segment-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
