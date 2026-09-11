package com.klyndyuk.paymentservice.kafka;

import com.klyndyuk.paymentservice.kafka.event.CreatePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String TOPIC = "create-payment";

    private final KafkaTemplate<String, CreatePaymentEvent> kafkaTemplate;

    public void send(CreatePaymentEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        );
    }
}