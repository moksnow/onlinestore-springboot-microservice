package com.moksnow.orderservice.controller;

import com.moksnow.orderservice.dto.OrderRequest;
import com.moksnow.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOder(orderRequest);
        return "Order Placed successfully";
    }
}
