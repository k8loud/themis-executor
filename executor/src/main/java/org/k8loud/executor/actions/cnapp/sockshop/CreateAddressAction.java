package org.k8loud.executor.actions.cnapp.sockshop;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;

import java.util.Map;

public class CreateAddressAction extends SockShopAction {
    private String username;
    private String password;
    private String id;
    private String country;
    private String city;
    private String postcode;
    private String street;
    private String number;

    public CreateAddressAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public CreateAddressAction(SockShopService sockShopService, String applicationUrl,
                               String username, String password, String id, String country, String city,
                               String postcode, String street, String number) {
        super(sockShopService, applicationUrl);
        this.username = username;
        this.password = password;
        this.id = id; // The new address will be assigned to the user with this id
        this.country = country;
        this.city = city;
        this.postcode = postcode;
        this.street = street;
        this.number = number;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        username = params.getRequiredParam("username");
        password = params.getRequiredParam("password");
        id = params.getRequiredParam("id");
        country = params.getRequiredParam("country");
        city = params.getRequiredParam("city");
        postcode = params.getRequiredParam("postcode");
        street = params.getRequiredParam("street");
        number = params.getRequiredParam("number");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.createAddress(applicationUrl, username, password, id, country, city, postcode, street,
                number);
    }
}
