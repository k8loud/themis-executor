package org.k8loud.executor.actions.cnapp.sockshop;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;

import java.util.Map;

@EqualsAndHashCode
public class DeleteAddressAction extends SockShopAction {
    private String username;
    private String password;
    private String addressId;

    public DeleteAddressAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public DeleteAddressAction(SockShopService sockShopService, String applicationUrl,
                               String username, String password, String addressId) {
        super(sockShopService, applicationUrl);
        this.username = username;
        this.password = password;
        this.addressId = addressId;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        username = params.getRequiredParam("username");
        password = params.getRequiredParam("password");
        addressId = params.getRequiredParam("addressId");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.deleteAddress(applicationUrl, username, password, addressId);
    }
}
