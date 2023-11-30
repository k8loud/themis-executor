package org.k8loud.executor.datastorage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "data-storage")
public class DataStorageProperties {
    private String rootPath;
    private boolean removalPermitted;
}
