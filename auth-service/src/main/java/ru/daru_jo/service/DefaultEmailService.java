package ru.daru_jo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Service
public class DefaultEmailService{

    @Value("${spring.mail.username}")
    private String senderEmail;
    public JavaMailSender emailSender;

    @Autowired public void setEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleEmail(String toAddress, String subject, String message) throws RuntimeException {
        sendSimpleEmail(new String[]{toAddress},subject, message);
    }

    public void sendSimpleEmail(String[] toAddress, String subject, String message) throws RuntimeException {

        try {
            sendEmailWithAttachment(toAddress, subject, message, null, null);
        } catch (FileNotFoundException | MessagingException ex) {
            log.error("Не получилось отправить письмо", ex);
        }
    }

    public void sendEmailWithAttachment(String[] toAddress, String subject, String message, String fileName, byte[] file) throws FileNotFoundException, MessagingException {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(toAddress);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
            if (fileName != null && file != null) {
                messageHelper.addAttachment(fileName, () -> new ByteArrayInputStream(file));
            }
            emailSender.send(mimeMessage);
        } catch (MailSendException ex) {
            throw new ResourceNotFoundRunTime(ex.getMessage());
        }
    }
}
