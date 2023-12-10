package org.k8loud.executor.actions.cnapp.mail;

import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.mail.MailService;
import org.k8loud.executor.model.Params;

import java.util.HashMap;
import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

public class SendMailAction extends MailAction {
    private Map<String, String> imageTitleToPath;

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
        this.imageTitleToPath = params.getOptionalParamAsMap("imageTitleToPath", new HashMap<>());
    }

    @Override
    protected Map<String, Object> executeBody() throws MailException {
        String result = String.format("Sent mail: senderDisplayName = '%s', receiver = %s, subject = '%s', " +
                "content = '%s'", senderDisplayName, receiver, subject, content);
        if (imageTitleToPath == null || imageTitleToPath.isEmpty()) {
            mailService.sendMail(receiver, senderDisplayName, subject, content).join();
            return resultMap(result);
        } else {
            mailService.sendMailWithEmbeddedImages(receiver, senderDisplayName, subject, content, imageTitleToPath).join();
            return resultMap(String.format("%s, imageTitleToPath = '%s'", result, imageTitleToPath));
        }
    }
}
