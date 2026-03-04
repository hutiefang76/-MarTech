package cn.javahome.frank.cdp.connector.mysql.cdc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 简化Feign：用于演示跨服务调用能力。
 */
@FeignClient(name = "cdp-openapi-service", url = "${demo.remote.openapi-url:http://localhost:19170}")
public interface ConnectorMysqlCdcDemoFeignClient {

    @GetMapping("/health")
    Map<String, Object> health();
}
