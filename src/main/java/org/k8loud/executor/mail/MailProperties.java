package org.k8loud.executor.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host;
    private int port = 587;
    private String address;
    private String password;
}
