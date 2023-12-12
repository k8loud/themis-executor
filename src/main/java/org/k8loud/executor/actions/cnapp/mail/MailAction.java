package org.k8loud.executor.actions.cnapp.mail;

import lombok.AllArgsConstructor;
import org.k8loud.executor.actions.cnapp.CNAppAction;
import org.k8loud.executor.cnapp.mail.MailService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.model.Params;

@AllArgsConstructor
public abstract class MailAction extends CNAppAction {
    protected MailService mailService;
    protected String receiver;
    protected String senderDisplayName;
    protected String subject;
    protected String content;

    public MailAction(Params params, MailService mailService) throws ActionException {
        super(params);
        this.mailService = mailService;
    }

    @Override
    public void unpackParams(Params params) {
        receiver = params.getRequiredParam("receiver");
        senderDisplayName = params.getRequiredParam("senderDisplayName");
        subject = params.getRequiredParam("subject");
        content = params.getRequiredParam("content");
        unpackAdditionalParams(params);
    }

    protected abstract void unpackAdditionalParams(Params params);
}
