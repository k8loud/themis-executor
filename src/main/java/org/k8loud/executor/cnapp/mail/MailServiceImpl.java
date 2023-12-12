package org.k8loud.executor.cnapp.mail;

import jakarta.activation.FileDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.k8loud.executor.exception.code.MailExceptionCode.FAILED_TO_SEND_MAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailProperties mailProperties;
    private final MailerProvider mailerProvider;

    @Override
    public CompletableFuture<Void> sendMail(String receiver, String senderDisplayName, String subject, String content)
            throws org.k8loud.executor.exception.MailException {
        return doSendMail(getMailBase(receiver, senderDisplayName, subject)
                .withPlainText(content)
                .buildEmail());
    }

    @Override
    public CompletableFuture<Void> sendMailWithEmbeddedImages(String receiver, String senderDisplayName, String subject,
                                                              String content, Map<String, String> imageTitleToPath)
            throws org.k8loud.executor.exception.MailException {
        EmailPopulatingBuilder emailBuilder = getMailBase(receiver, senderDisplayName, subject);
        StringBuilder htmlText = new StringBuilder();
        htmlText.append(String.format("<p>%s</p>", content.replace("\n", "<br>")));
        for (Map.Entry<String, String> e : imageTitleToPath.entrySet()) {
            final String title = e.getKey();
            final String path = e.getValue();
            log.info("Adding embedded image: title = '{}', path = '{}'", title, path);
            emailBuilder.withEmbeddedImage(title, new FileDataSource(path));
            htmlText.append(String.format("<p>%s</p><img src='cid:%s'><br/>%n", title, title));
        }
        return doSendMail(emailBuilder
                .withHTMLText(htmlText.toString())
                .buildEmail());
    }

    private EmailPopulatingBuilder getMailBase(String receiver, String senderDisplayName, String subject) {
        log.info("Creating mail base: sender = {}, senderDisplayName = '{}', receiver = {}, subject = '{}'",
                mailProperties.getAddress(), receiver, senderDisplayName, subject);
        return EmailBuilder.startingBlank()
                .from(senderDisplayName, mailProperties.getAddress())
                .to(receiver)
                .withSubject(subject);
    }

    private CompletableFuture<Void> doSendMail(Email email) throws org.k8loud.executor.exception.MailException {
        try {
            return mailerProvider.getMailer()
                    .sendMail(email);
        } catch (MailException e) {
            throw new org.k8loud.executor.exception.MailException(e, FAILED_TO_SEND_MAIL);
        }
    }
}
