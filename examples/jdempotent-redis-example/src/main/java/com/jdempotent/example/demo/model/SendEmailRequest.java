package com.jdempotent.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class SendEmailRequest implements Serializable {
    private String email;
    private String subject;
    private String message;
}
