package org.k8loud.executor.mail;

import org.k8loud.executor.exception.MailException;

public interface MailService {
    void sendMail(String receiverAddress, String subject, String content) throws MailException;
}
