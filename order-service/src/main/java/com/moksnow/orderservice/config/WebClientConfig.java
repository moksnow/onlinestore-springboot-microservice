package com.moksnow.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //recommended after spring framework 5 use webflux web client instead of rest template
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
