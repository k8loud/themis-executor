package org.k8loud.executor.actions.cnapp.mail;

import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.mail.MailService;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

// TODO: merge with SendMailAction
public class SendMailWithEmbeddedImagesAction extends MailAction {
    private Map<String, String> imageTitleToPath;

    public SendMailWithEmbeddedImagesAction(Params params, MailService mailService) throws ActionException {
        super(params, mailService);
    }

    @Builder
    public SendMailWithEmbeddedImagesAction(MailService mailService, String receiver, String senderDisplayName,
                                            String subject, String content,
                                            Map<String, String> imageTitleToPath) {
        super(mailService, receiver, senderDisplayName, subject, content);
        this.imageTitleToPath = imageTitleToPath;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        this.imageTitleToPath = params.getRequiredParamAsMap("imageTitleToPath");
    }

    @Override
    protected Map<String, String> executeBody() throws MailException {
        mailService.sendMailWithEmbeddedImages(receiver, senderDisplayName, subject, content, imageTitleToPath).join();
        return resultMap(String.format("Sent mail: senderDisplayName = '%s', receiver = %s, subject = '%s', " +
                "content = '%s', imageTitleToPath = '%s'",
                senderDisplayName, receiver, subject, content, imageTitleToPath));
    }
}
