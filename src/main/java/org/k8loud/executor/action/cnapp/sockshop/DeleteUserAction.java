package org.k8loud.executor.action.cnapp.sockshop;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;

import java.util.Map;

public class DeleteUserAction extends SockShopAction {
    private String userId;

    public DeleteUserAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public DeleteUserAction(SockShopService sockShopService, String applicationUrl,
                            String userId) {
        super(sockShopService, applicationUrl);
        this.userId = userId;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        userId = params.getRequiredParam("userId");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.deleteUser(applicationUrl, userId);
    }
}
