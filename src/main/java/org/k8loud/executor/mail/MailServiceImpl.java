package org.k8loud.executor.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.k8loud.executor.exception.MailException;
import org.springframework.stereotype.Service;

import static org.k8loud.executor.exception.code.MailExceptionCode.*;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailProperties mailProperties;
    private final MailSessionProvider mailSessionProvider;

    @Override
    public void sendMail(String receiverAddress, String subject, String content) throws MailException {
        Message message = new MimeMessage(mailSessionProvider.getSession());
        try {
            message.setFrom(new InternetAddress(mailProperties.getAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverAddress));
        } catch (MessagingException e) {
            throw new MailException(e, CREATE_MAIL_ADDRESS_FAILURE);
        }

        try {
            message.setSubject(subject);
        } catch (MessagingException e) {
            throw new MailException(e, SET_SUBJECT_FAILURE);
        }

        MimeBodyPart bodyPart = new MimeBodyPart();
        try {
            bodyPart.setContent(content, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);
        } catch (MessagingException e) {
            throw new MailException(e, SET_CONTENT_FAILURE);
        }

        try {
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException(e, SEND_FAILURE);
        }
    }
}
