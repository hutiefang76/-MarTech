package cn.javahome.frank.cdp.tag.task.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TagTaskClientConfig {

    @Bean
    public RestClient flinkRestClient(RestClient.Builder builder,
                                      @Value("${cdp.flink-job.base-url:http://localhost:19190}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
