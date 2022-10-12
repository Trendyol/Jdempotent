package com.jdempotent.example.demo.controller;

import com.jdempotent.example.demo.exception.InvalidEmailAddressException;
import com.jdempotent.example.demo.model.SendEmailRequest;
import com.jdempotent.example.demo.model.SendEmailResponse;
import com.jdempotent.example.demo.service.MailSenderService;
import com.trendyol.jdempotent.core.annotation.JdempotentResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.trendyol.jdempotent.core.annotation.JdempotentRequestPayload;
import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailSenderService mailSenderService;

    @PostMapping("/send-email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @JdempotentResource(cachePrefix = "MailController.sendEmail")
    public SendEmailResponse sendEmail(@RequestBody SendEmailRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
        } catch (MessagingException e) {
            log.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
            throw new RuntimeException();
        }

        return new SendEmailResponse("We will send your message");
    }

    @PostMapping("/send-email-header")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @JdempotentResource(cachePrefix = "MailController.sendEmail")
    public SendEmailResponse sendEmail(
            @JdempotentRequestPayload @RequestHeader("x-idempotency-key") String idempotencyKey,
            @RequestBody SendEmailRequest request)
    {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
        } catch (Exception e) {
            log.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
            throw new RuntimeException();
        }

        return new SendEmailResponse("We will send your message");
    }
    @PostMapping("/send-email-header-body")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @JdempotentResource(cachePrefix = "MailController.sendEmail")
    public SendEmailResponse sendEmailWithHeaderAndBody(
            @JdempotentRequestPayload @RequestHeader("x-idempotency-key") String idempotencyKey,
            @JdempotentRequestPayload @RequestBody SendEmailRequest request
    ) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            log.info("sendEmailWithHeaderAndBody#idempotencyKey: " + request.getIdempotencyKey());
            mailSenderService.sendMail(request);
            return new SendEmailResponse("We will send your message");
        } catch (Exception e) {
            log.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
            throw new RuntimeException();
        }
    }

    @PostMapping("v2/send-email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @JdempotentResource(
            cachePrefix = "MailController.sendEmailV2",
            ttl = 1,
            ttlTimeUnit = TimeUnit.MINUTES)
    public SendEmailResponse sendEmailV2(@JdempotentRequestPayload @RequestBody SendEmailRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new InvalidEmailAddressException();
        }

        try {
            mailSenderService.sendMail(request);
        } catch (MessagingException e) {
            log.debug("MailSenderService.sendEmail() throw exception: {} request: {} ", e, request);
            throw new RuntimeException();
        }

        return new SendEmailResponse("We will send your message");
    }
}
