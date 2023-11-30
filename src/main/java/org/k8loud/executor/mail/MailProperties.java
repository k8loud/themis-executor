package org.k8loud.executor.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host = "poczta.int.pl";
    private String port = "465";
    private String username;
    private String password;

    public String getAddress() {
        return username + "@" + host;
    }
}
