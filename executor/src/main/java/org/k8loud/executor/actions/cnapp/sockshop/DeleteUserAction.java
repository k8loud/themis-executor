package org.k8loud.executor.actions.cnapp.sockshop;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;

import java.util.Map;

public class DeleteUserAction extends SockShopAction {
    private String id;

    public DeleteUserAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public DeleteUserAction(SockShopService sockShopService, String applicationUrl,
                            String id) {
        super(sockShopService, applicationUrl);
        this.id = id;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        id = params.getRequiredParam("id");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.deleteUser(applicationUrl, id);
    }
}
