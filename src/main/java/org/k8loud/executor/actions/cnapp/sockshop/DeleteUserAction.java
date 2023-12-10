package org.k8loud.executor.actions.cnapp.sockshop;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.model.Params;

import java.util.Map;

@EqualsAndHashCode
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
    protected Map<String, Object> executeBody() throws CustomException {
        return sockShopService.deleteUser(applicationUrl, userId);
    }
}
