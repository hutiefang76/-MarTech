package cn.javahome.frank.cdp.flink.job.service.impl;

import cn.javahome.frank.cdp.flink.job.entity.FlinkJobEntity;
import cn.javahome.frank.cdp.flink.job.feign.FlinkJobDemoFeignClient;
import cn.javahome.frank.cdp.flink.job.service.FlinkJobService;
import cn.javahome.frank.cdp.flink.job.util.FlinkJobUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class FlinkJobServiceImpl implements FlinkJobService {

    private final FlinkJobDemoFeignClient demoFeignClient;

    public FlinkJobServiceImpl(FlinkJobDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        FlinkJobEntity entity = new FlinkJobEntity(
                FlinkJobUtil.buildId("flink-job"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-flink-job-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
