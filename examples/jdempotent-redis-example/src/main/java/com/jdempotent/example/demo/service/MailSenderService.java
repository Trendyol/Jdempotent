package com.jdempotent.example.demo.service;

import com.jdempotent.example.demo.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class MailSenderService {

    private JavaMailSender javaMailSender;

    @Autowired
    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${email.from.address}")
    private String fromAddress;

    public void sendMail(SendEmailRequest emailRequest) throws MessagingException {
        sendMailMultipart(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getMessage(), null);
    }

    public void sendMail(SendEmailRequest emailRequest, File file) throws MessagingException {
        sendMailMultipart(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getMessage(), file);
    }

    private void sendMailMultipart(String toEmail, String subject, String message, File file) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(fromAddress);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(message);

        if (file != null) {
            helper.addAttachment(file.getName(), file);
        }
        javaMailSender.send(mimeMessage);
    }
}
