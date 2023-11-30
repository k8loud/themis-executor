package org.k8loud.executor.actions.cnapp.sockshop;

import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.model.Params;

import java.util.Map;

public class NotifyCustomersAction extends SockShopAction {
    private String senderDisplayName;
    private String subject;
    private String content;

    public NotifyCustomersAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public NotifyCustomersAction(SockShopService sockShopService, String applicationUrl,
                                 String senderDisplayName, String subject, String content) {
        super(sockShopService, applicationUrl);
        this.senderDisplayName = senderDisplayName;
        this.subject = subject;
        this.content = content;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        this.senderDisplayName = params.getRequiredParam("senderDisplayName");
        this.subject = params.getRequiredParam("subject");
        this.content = params.getRequiredParam("content");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return sockShopService.notifyCustomers(applicationUrl, senderDisplayName, subject, content);
    }
}
