package org.k8loud.executor.mail;

import org.k8loud.executor.exception.MailException;

import java.util.Map;

public interface MailService {
    void sendMail(String receiver, String senderDisplayName, String subject, String content) throws MailException;

    void sendMailWithEmbeddedImages(String receiver, String senderDisplayName, String subject, String content,
                                   Map<String, String> imageTitleToPath) throws MailException;
}
