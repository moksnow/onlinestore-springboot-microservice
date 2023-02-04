package com.moksnow.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //recommended after spring framework 5 use webflux web client instead of rest template
    @Bean
    //Failed to resolve 'inventory-service' after 6 queries
    //when you have many instance of a service you must use of this annotation to you can find one of them
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
