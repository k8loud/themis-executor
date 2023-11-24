package org.k8loud.executor.cnapp.sockshop.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAddressParams {
    private String id; // The new address will be assigned to the user with this id
    private String country;
    private String city;
    private String postcode;
    private String street;
    private String number;
}
