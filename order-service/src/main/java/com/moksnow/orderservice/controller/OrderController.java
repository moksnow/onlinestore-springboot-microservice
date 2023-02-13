package com.moksnow.orderservice.controller;

import com.moksnow.orderservice.dto.OrderRequest;
import com.moksnow.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;


    private final Tracer tracer;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String>  placeOrder(@RequestBody OrderRequest orderRequest) {
//        return CompletableFuture.supplyAsync(() -> orderService.placeOder(orderRequest));
        log.info("Started Place Order.");
        Span echoSpan = tracer.nextSpan().name("calling-echo").start();

        String result = orderService.placeOder(orderRequest);
        log.info(result);
        echoSpan.end();
        return  CompletableFuture.supplyAsync(() -> orderService.placeOder(orderRequest));

    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time");
    }
}
