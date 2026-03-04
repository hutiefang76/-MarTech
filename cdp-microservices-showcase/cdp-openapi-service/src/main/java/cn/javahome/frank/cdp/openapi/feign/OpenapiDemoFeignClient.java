package cn.javahome.frank.cdp.openapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 简化Feign：用于演示跨服务调用能力。
 */
@FeignClient(name = "cdp-identity-service", url = "${demo.remote.identity-url:http://localhost:19120}")
public interface OpenapiDemoFeignClient {

    @GetMapping("/health")
    Map<String, Object> health();
}
