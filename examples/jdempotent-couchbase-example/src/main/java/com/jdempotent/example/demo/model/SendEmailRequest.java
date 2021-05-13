package com.jdempotent.example.demo.model;

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
    private String email;
    private String subject;
    private String message;
}