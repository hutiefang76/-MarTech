package cn.javahome.frank.cdp.tag.service.impl;

import cn.javahome.frank.cdp.tag.entity.TagEntity;
import cn.javahome.frank.cdp.tag.feign.TagDemoFeignClient;
import cn.javahome.frank.cdp.tag.service.TagService;
import cn.javahome.frank.cdp.tag.util.TagUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagDemoFeignClient demoFeignClient;

    public TagServiceImpl(TagDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        TagEntity entity = new TagEntity(
                TagUtil.buildId("tag"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-tag-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
