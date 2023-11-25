package org.k8loud.executor.hephaestus;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hephaestus")
public class HephaestusProperties {
    private String url;
    private String selectedEndpoint = "/hephaestus/metrics/selected";
}
