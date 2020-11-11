package com.jdempotent.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ErrorResponse implements Serializable {
    private String message;
    private List<String> details;

    public ErrorResponse(String message){
        this.message = message;
    }
}
