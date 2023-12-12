package org.k8loud.executor.cnapp.mail;

import lombok.RequiredArgsConstructor;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailerProvider {
    private final MailProperties mailProperties;
    private Mailer mailer;

    public Mailer getMailer() {
        if (mailer == null) {
            mailer = createMailer();
        }
        return mailer;
    }

    private Mailer createMailer() {
        return MailerBuilder.withSMTPServer(mailProperties.getHost(), mailProperties.getPort(),
                        mailProperties.getAddress(), mailProperties.getPassword())
                .buildMailer();
    }
}
