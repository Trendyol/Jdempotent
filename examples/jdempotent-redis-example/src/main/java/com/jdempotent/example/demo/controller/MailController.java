package com.jdempotent.example.demo.controller;

import com.Jdempotent.core.annotation.IdempotentResource;
import com.jdempotent.example.demo.exception.InvalidEmailAddressException;
import com.jdempotent.example.demo.model.SendEmailRequest;
import com.jdempotent.example.demo.model.SendEmailResponse;
import com.jdempotent.example.demo.service.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
public class MailController {

    @Autowired
    private MailSenderService mailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @PostMapping("/send-email")
    @IdempotentResource(cachePrefix = "MailController")
    public ResponseEntity<SendEmailResponse> sendEmail(@RequestBody SendEmailRequest request){
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
        } catch (MessagingException e) {
            logger.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e,request);
        }

        return new ResponseEntity(new SendEmailResponse("We will send your message"), HttpStatus.ACCEPTED);
    }
}
