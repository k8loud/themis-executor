package org.k8loud.executor.actions.cnapp.sockshop;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;

import java.util.Map;

public class DeleteAddressAction extends SockShopAction {
    private String username;
    private String password;
    private String id; // id of the address to be deleted

    public DeleteAddressAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public DeleteAddressAction(SockShopService sockShopService, String applicationUrl,
                               String username, String password, String id) {
        super(sockShopService, applicationUrl);
        this.username = username;
        this.password = password;
        this.id = id;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        username = params.getRequiredParam("username");
        password = params.getRequiredParam("password");
        id = params.getRequiredParam("id");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.deleteAddress(applicationUrl, username, password, id);
    }
}
