package cn.javahome.frank.cdp.profile.service.impl;

import cn.javahome.frank.cdp.profile.entity.ProfileEntity;
import cn.javahome.frank.cdp.profile.feign.ProfileDemoFeignClient;
import cn.javahome.frank.cdp.profile.service.ProfileService;
import cn.javahome.frank.cdp.profile.util.ProfileUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileDemoFeignClient demoFeignClient;

    public ProfileServiceImpl(ProfileDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        ProfileEntity entity = new ProfileEntity(
                ProfileUtil.buildId("profile"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-profile-service",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
