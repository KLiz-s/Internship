package com.klyndyuk.orderservice.kafka;

import com.klyndyuk.orderservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.orderservice.service.interfaces.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Test
    void consume_shouldHandlePaymentEvent() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CreatePaymentEvent event =
                new CreatePaymentEvent(
                        paymentId,
                        orderId,
                        "SUCCESS"
                );

        paymentEventConsumer.consume(event);

        verify(orderService).handlePaymentEvent(event);
    }
}