package com.moksnow.orderservice.service;

import com.moksnow.orderservice.dto.InventoryResponse;
import com.moksnow.orderservice.dto.OrderLineItemsDto;
import com.moksnow.orderservice.dto.OrderRequest;
import com.moksnow.orderservice.event.OrderPlacedEvent;
import com.moksnow.orderservice.model.Order;
import com.moksnow.orderservice.model.OrderLineItems;
import com.moksnow.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;


    public String placeOder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);


        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

//        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
//        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {

            // call inventory service, and place order if product is in stock
            InventoryResponse[] result = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(result).
                    allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);

                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed successfully";
            } else {
                throw new IllegalArgumentException("Product is not ins stock, please try again later");
            }
//        } finally {
//
//            inventoryServiceLookup.end();
//        }


    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
