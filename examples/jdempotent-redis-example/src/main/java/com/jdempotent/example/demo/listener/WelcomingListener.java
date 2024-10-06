package com.jdempotent.example.demo.listener;

import com.trendyol.jdempotent.core.annotation.JdempotentResource;
import com.jdempotent.example.demo.exception.RetryIdempotentRequestException;
import com.jdempotent.example.demo.model.SendEmailRequest;
import com.jdempotent.example.demo.service.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class WelcomingListener {

    @Autowired
    private MailSenderService mailSenderService;
    private static final Logger logger = LoggerFactory.getLogger(WelcomingListener.class);

    @Value("${template.welcoming.message}")
    private String message;

    @Value("${template.welcoming.subject}")
    private String subject;

    //@KafkaListener(topics = "trendyol.mail.welcome", groupId = "group_id")
    @JdempotentResource
    public void consumeMessage(String emailAdress) {
        SendEmailRequest request = SendEmailRequest.builder()
                .email(message)
                .subject(subject)
                .build();

        try {
            mailSenderService.sendMail(request);
        } catch (MessagingException e) {
            logger.error("MailSenderService.sendEmail() throw exception {} event: {} ", e, emailAdress);

            // Throwing any exception is enough to delete from redis. When successful, it will not be deleted from redis and will be idempotent.
            throw new RetryIdempotentRequestException(e);
        }
    }

}