package org.k8loud.executor.mail;

import jakarta.activation.FileDataSource;
import lombok.RequiredArgsConstructor;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.k8loud.executor.exception.code.MailExceptionCode.FAILED_TO_SEND_MAIL;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailProperties mailProperties;
    private final MailerProvider mailerProvider;

    @Override
    public void sendMail(String receiver, String senderDisplayName, String subject, String content)
            throws org.k8loud.executor.exception.MailException {
            doSendMail(getMailBase(receiver, senderDisplayName, subject, content).buildEmail());
    }

    @Override
    public void sendMailWithEmbeddedImages(String receiver, String senderDisplayName, String subject, String content,
                                          Map<String, String> imageTitleToPath)
            throws org.k8loud.executor.exception.MailException {
        EmailPopulatingBuilder emailBuilder = getMailBase(receiver, senderDisplayName, subject, content);
        for (Map.Entry<String, String> e : imageTitleToPath.entrySet()) {
            emailBuilder.withEmbeddedImage(e.getKey(), new FileDataSource(e.getValue()));
        }
        doSendMail(emailBuilder.buildEmail());
    }

    private EmailPopulatingBuilder getMailBase(String receiver, String senderDisplayName, String subject,
                                                String content) {
        return EmailBuilder.startingBlank()
                .from(senderDisplayName, mailProperties.getAddress())
                .to(receiver)
                .withSubject(subject)
                .withPlainText(content);
    }

    private void doSendMail(Email email) throws org.k8loud.executor.exception.MailException {
        try {
            mailerProvider.getMailer()
                    .sendMail(email);
        } catch (MailException e) {
            throw new org.k8loud.executor.exception.MailException(e, FAILED_TO_SEND_MAIL);
        }
    }
}
