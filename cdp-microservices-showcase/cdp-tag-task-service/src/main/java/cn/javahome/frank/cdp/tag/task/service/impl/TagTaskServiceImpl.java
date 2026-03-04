package cn.javahome.frank.cdp.tag.task.service.impl;

import cn.javahome.frank.cdp.tag.task.entity.TagTaskEntity;
import cn.javahome.frank.cdp.tag.task.feign.TagTaskDemoFeignClient;
import cn.javahome.frank.cdp.tag.task.service.TagTaskService;
import cn.javahome.frank.cdp.tag.task.util.TagTaskUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class TagTaskServiceImpl implements TagTaskService {

    private final TagTaskDemoFeignClient demoFeignClient;

    public TagTaskServiceImpl(TagTaskDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        TagTaskEntity entity = new TagTaskEntity(
                TagTaskUtil.buildId("tag-task"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-tag-task-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
