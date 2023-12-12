package org.k8loud.executor.cnapp.mail;

import org.k8loud.executor.exception.MailException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface MailService {
    CompletableFuture<Void> sendMail(String receiver, String senderDisplayName, String subject, String content)
            throws MailException;

    CompletableFuture<Void> sendMailWithEmbeddedImages(String receiver, String senderDisplayName, String subject,
                                                       String content, Map<String, String> imageTitleToPath)
            throws MailException;
}
