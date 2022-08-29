package com.jdempotent.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class SendEmailResponse implements Serializable {

    private static final long serialVersionUID = 7930732926638008763L;

    private String message;
}
