package org.k8loud.executor.actions.cnapp.mail;

import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.mail.MailService;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

public class SendMailAction extends MailAction {
    public SendMailAction(Params params, MailService mailService) throws ActionException {
        super(params, mailService);
    }

    @Builder
    public SendMailAction(MailService mailService, String receiver, String senderDisplayName, String subject,
                          String content) throws ActionException {
        super(mailService, receiver, senderDisplayName, subject, content);
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        // empty
    }

    @Override
    protected Map<String, String> executeBody() throws MailException {
        mailService.sendMail(receiver, senderDisplayName, subject, content).join();
        return resultMap(String.format("Sent mail: senderDisplayName = '%s', receiver = %s, subject = '%s', " +
                        "content = '%s'", senderDisplayName, receiver, subject, content));
    }
}
