package org.k8loud.executor.actions.cnapp.sockshop;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.model.Params;

import java.util.Map;

@EqualsAndHashCode
public class CreateAddressAction extends SockShopAction {
    private String username;
    private String password;
    private String userId; // The new address will be assigned to the user with this id
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
                               String username, String password, String userId, String country, String city,
                               String postcode, String street, String number) {
        super(sockShopService, applicationUrl);
        this.username = username;
        this.password = password;
        this.userId = userId;
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
        userId = params.getRequiredParam("userId");
        country = params.getRequiredParam("country");
        city = params.getRequiredParam("city");
        postcode = params.getRequiredParam("postcode");
        street = params.getRequiredParam("street");
        number = params.getRequiredParam("number");
    }

    @Override
    protected Map<String, Object> executeBody() throws CustomException {
        return sockShopService.createAddress(applicationUrl, username, password, userId, country, city, postcode,
                street, number);
    }
}
