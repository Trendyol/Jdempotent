package com.jdempotent.example.demo.model;

import com.trendyol.jdempotent.core.annotation.JdempotentId;
import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SendEmailRequest implements Serializable {
    @JdempotentId
    private String idempotencyKey;
    private String email;
    private String subject;
    private String message;
}
