package org.k8loud.executor.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailSessionProvider {
    private final MailProperties mailProperties;
    private Session session;

    public Session getSession() {
        if (session == null) {
            session = createSession();
        }
        return session;
    }

    private Session createSession() {
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.host", mailProperties.getHost());
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.port", mailProperties.getPort());
        prop.put("mail.smtp.socketFactory.port", mailProperties.getPort());
        prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        prop.put("mail.smtp.starttls.enable", "true");
//        prop.put("mail.smtp.ssl.trust", mailProperties.getHost());

//        prop.setProperty("mail.host", "smtp.gmail.com");
//        prop.put("mail.smtp.auth", "true");
//        prop.put("mail.smtp.port", "465");
//        prop.put("mail.debug", "true");


        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getUsername(), mailProperties.getPassword());
            }
        });
    }
}
