package org.k8loud.executor.actions.cnapp.sockshop;

import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.model.Params;

import java.util.Map;

public class NotifyCustomersAction extends SockShopAction {
    private String message;

    public NotifyCustomersAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public NotifyCustomersAction(SockShopService sockShopService, String applicationUrl,
                                 String message) {
        super(sockShopService, applicationUrl);
        this.message = message;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        this.message = params.getRequiredParam("message");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.notifyCustomers(applicationUrl, message);
    }
}
