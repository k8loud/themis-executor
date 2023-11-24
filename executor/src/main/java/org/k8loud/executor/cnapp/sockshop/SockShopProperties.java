package org.k8loud.executor.cnapp.sockshop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sockshop")
public class SockShopProperties {
    private String loginUserUrlSupplement = "login";
    private String registerUserUrlSupplement = "register";
    private String customersUrlSupplement = "customers";
    private String addressesUrlSupplement = "addresses";
}
