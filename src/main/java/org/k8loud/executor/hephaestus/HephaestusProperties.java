package org.k8loud.executor.hephaestus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
@Component
@ConfigurationProperties(prefix = "hephaestus")
public class HephaestusProperties {
    private String url;
    private String selectedEndpoint = "/hephaestus/metrics/selected";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
