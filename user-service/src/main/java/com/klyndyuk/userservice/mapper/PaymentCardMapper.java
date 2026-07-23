package com.klyndyuk.userservice.mapper;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCard toEntity(CreatePaymentCardRequest request);

    PaymentCardResponse toResponse(PaymentCard paymentCard);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdatePaymentCardRequest request,
                      @MappingTarget PaymentCard paymentCard);
}
