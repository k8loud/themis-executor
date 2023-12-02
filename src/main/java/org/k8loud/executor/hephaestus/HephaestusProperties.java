package org.k8loud.executor.hephaestus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "hephaestus")
public class HephaestusProperties {
    private String url = "localhost:8080";
    private String selectedEndpoint = "/hephaestus/metrics/selected";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
