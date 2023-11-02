package org.k8loud.executor.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "data-storage")
public class DataStorageProperties {
    private String rootPath;
    private boolean removalPermitted;
}
