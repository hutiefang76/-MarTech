package cn.javahome.frank.cdp.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 服务入口：用于本地展示微服务边界与调用链。
 */
@EnableFeignClients
@SpringBootApplication
public class CdpIdentityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CdpIdentityServiceApplication.class, args);
    }
}


