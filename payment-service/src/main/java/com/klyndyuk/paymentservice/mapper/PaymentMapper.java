package com.klyndyuk.paymentservice.mapper;

import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Payment fromCreatePaymentRequest(CreatePaymentRequest createPaymentRequest);

    PaymentResponse toPaymentResponse(Payment payment);
}
