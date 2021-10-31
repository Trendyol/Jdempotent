package com.jdempotent.example.demo.controller;

import com.jdempotent.example.demo.exception.InvalidEmailAddressException;
import com.jdempotent.example.demo.model.SendEmailRequest;
import com.jdempotent.example.demo.model.SendEmailResponse;
import com.jdempotent.example.demo.service.MailSenderService;
import com.trendyol.jdempotent.core.annotation.IdempotentHeaderKey;
import com.trendyol.jdempotent.core.annotation.IdempotentResource;
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
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class MailController {

    @Autowired
    private MailSenderService mailSenderService;
    
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @PostMapping("/send-email")
    //@IdempotentResource(cachePrefix = "MailController.sendEmail")
    @IdempotentHeaderKey(uuid="x-idempotency-key", ttl=30, ttlTimeUnit=TimeUnit.SECONDS)
    public ResponseEntity<SendEmailResponse> sendEmail(
            @RequestHeader(value = "x-idempotency-key", required = false) String optionalHeader,            
            @RequestBody SendEmailRequest request) {
        logger.info("x-idempotency-key " + optionalHeader);
        HttpStatus status;
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
            status = HttpStatus.ACCEPTED;
        } catch (Exception e) {
            logger.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity(new SendEmailResponse("We will send your message"), status);
    }

    @PostMapping("v2/send-email")
    @IdempotentResource(
            cachePrefix = "MailController.sendEmailV2",
            ttl = 1,
            ttlTimeUnit = TimeUnit.MINUTES)
    public ResponseEntity<SendEmailResponse> sendEmailV2(@RequestBody SendEmailRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
        } catch (MessagingException e) {
            logger.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
        }

        return new ResponseEntity(new SendEmailResponse("We will send your message"), HttpStatus.ACCEPTED);
    }
}
