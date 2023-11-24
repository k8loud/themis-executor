package org.k8loud.executor.actions.cnapp.sockshop;

import org.k8loud.executor.model.Params;
import lombok.AllArgsConstructor;
import org.k8loud.executor.actions.cnapp.CNAppAction;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;

@AllArgsConstructor
public abstract class SockShopAction extends CNAppAction {
    protected SockShopService sockShopService;
    protected String applicationUrl;

    protected SockShopAction(Params params, SockShopService sockShopService)
            throws ActionException {
        super(params);
        this.sockShopService = sockShopService;
    }

    @Override
    public void unpackParams(Params params) {
        applicationUrl = params.getRequiredParam("applicationUrl");
        unpackAdditionalParams(params);
    }

    protected abstract void unpackAdditionalParams(Params params);
}
