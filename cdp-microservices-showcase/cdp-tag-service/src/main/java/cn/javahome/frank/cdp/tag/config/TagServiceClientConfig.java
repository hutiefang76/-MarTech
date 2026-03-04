package cn.javahome.frank.cdp.tag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TagServiceClientConfig {

    @Bean
    public RestClient tagTaskRestClient(RestClient.Builder builder,
                                        @Value("${cdp.tag-task.base-url:http://localhost:19141}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
