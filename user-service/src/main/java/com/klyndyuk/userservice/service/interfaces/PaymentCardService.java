package com.klyndyuk.userservice.service.interfaces;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentCardService {
    PaymentCardResponse create(CreatePaymentCardRequest request);

    PaymentCardResponse getById(UUID id);

    Page<PaymentCardResponse> getAll(String holder,
                                     Pageable pageable);

    List<PaymentCardResponse> getAllByUserId(UUID userId);

    PaymentCardResponse update(UUID id,
                               UpdatePaymentCardRequest request);

    void updateActive(UUID id, boolean active);

    void delete(UUID id);
}