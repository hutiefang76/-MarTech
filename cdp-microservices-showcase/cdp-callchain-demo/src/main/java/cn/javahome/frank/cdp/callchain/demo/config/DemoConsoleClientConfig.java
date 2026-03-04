package cn.javahome.frank.cdp.callchain.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Demo console remote clients for cross-service call-chain simulation.
 */
@Configuration
public class DemoConsoleClientConfig {

    @Bean
    public RestClient connectorControlClient(RestClient.Builder builder,
                                             @Value("${cdp.target.connector-control-base-url:http://localhost:19101}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient tagTaskClient(RestClient.Builder builder,
                                    @Value("${cdp.target.tag-task-base-url:http://localhost:19141}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient flinkJobClient(RestClient.Builder builder,
                                     @Value("${cdp.target.flink-job-base-url:http://localhost:19190}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}

