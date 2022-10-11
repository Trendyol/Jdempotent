package com.jdempotent.example.demo.service;

import com.jdempotent.example.demo.model.PrimeNumberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimeNumberService {
    public PrimeNumberResponse generatePrimeNumber(long qtd) {

        return PrimeNumberResponse.builder()
                .primesNumber(primeNumbersTill(qtd))
                .build();

    }

    public PrimeNumberResponse generatePrimeNumberWithSleep(long qtd) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("PrimeNumberService#generatePrimeNumberWithSleep: ", e);
        }
        return PrimeNumberResponse.builder()
                .primesNumber(primeNumbersTill(qtd))
                .build();

    }

    private List<Long> primeNumbersTill(long n) {
        return LongStream.rangeClosed(2, n)
                .filter(this::isPrime).boxed()
                .collect(Collectors.toList());
    }
    private boolean isPrime(long number) {
        return LongStream.rangeClosed(2, (long) (Math.sqrt(number)))
                .allMatch(n -> number % n != 0);
    }
}
