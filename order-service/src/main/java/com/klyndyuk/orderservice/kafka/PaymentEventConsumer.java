package com.klyndyuk.orderservice.kafka;

import com.klyndyuk.orderservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.orderservice.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "create-payment",
            groupId = "order-service"
    )
    public void consume(CreatePaymentEvent event) {

        orderService.handlePaymentEvent(event);
    }
}
