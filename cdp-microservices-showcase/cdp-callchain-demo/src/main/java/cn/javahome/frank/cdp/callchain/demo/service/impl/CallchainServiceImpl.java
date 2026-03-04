package cn.javahome.frank.cdp.callchain.demo.service.impl;

import cn.javahome.frank.cdp.callchain.demo.entity.CallchainEntity;
import cn.javahome.frank.cdp.callchain.demo.feign.CallchainDemoFeignClient;
import cn.javahome.frank.cdp.callchain.demo.service.CallchainService;
import cn.javahome.frank.cdp.callchain.demo.util.CallchainUtil;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 简化实现：演示 service -> feign -> entity -> util 的最小闭环。
 */
@Service
public class CallchainServiceImpl implements CallchainService {

    private final CallchainDemoFeignClient demoFeignClient;

    public CallchainServiceImpl(CallchainDemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    @Override
    public Map<String, Object> summary(String tenantId, String traceId) {
        CallchainEntity entity = new CallchainEntity(
                CallchainUtil.buildId("callchain"),
                tenantId,
                traceId,
                OffsetDateTime.now()
        );
        return Map.of(
                "module", "cdp-callchain-demo",
                "entity", entity,
                "remote", "feign-enabled"
        );
    }
}
