package com.klyndyuk.paymentservice.kafka;

import com.klyndyuk.paymentservice.kafka.event.CreatePaymentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private KafkaTemplate<String, CreatePaymentEvent> kafkaTemplate;

    @InjectMocks
    private PaymentEventProducer paymentEventProducer;

    @Test
    void send_shouldSendEventToCreatePaymentTopic() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CreatePaymentEvent event =
                new CreatePaymentEvent(
                        paymentId,
                        orderId,
                        "SUCCESS"
                );

        paymentEventProducer.send(event);

        verify(kafkaTemplate).send(
                eq("create-payment"),
                eq(orderId.toString()),
                eq(event)
        );
    }
}