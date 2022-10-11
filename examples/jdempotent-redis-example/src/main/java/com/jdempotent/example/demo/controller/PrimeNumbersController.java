package com.jdempotent.example.demo.controller;

import com.jdempotent.example.demo.model.PrimeNumberResponse;
import com.jdempotent.example.demo.service.PrimeNumberService;
import com.trendyol.jdempotent.core.annotation.JdempotentResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class PrimeNumbersController {

    private final PrimeNumberService primeNumberService;

    @GetMapping("/prime-number")
    @ResponseStatus(HttpStatus.OK)
    @JdempotentResource(cachePrefix = "PrimeNumber.generatePrimeNumber", ttl = 30, ttlTimeUnit = TimeUnit.SECONDS)
    public PrimeNumberResponse generatePrimeNumber(@RequestParam(required = false, defaultValue = "5", name = "quantityPrimeNumbers") long qtd){
        return primeNumberService.generatePrimeNumber(qtd);
    }

    @GetMapping("/prime-number-sleep")
    @ResponseStatus(HttpStatus.OK)
    @JdempotentResource(cachePrefix = "PrimeNumber.generatePrimeNumber", ttl = 30, ttlTimeUnit = TimeUnit.SECONDS)
    public PrimeNumberResponse generatePrimeNumberWithSleep(@RequestParam(required = false, defaultValue = "5", name = "quantityPrimeNumbers") long qtd){
        return primeNumberService.generatePrimeNumberWithSleep(qtd);
    }
}
