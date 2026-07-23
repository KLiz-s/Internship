package com.klyndyuk.userservice.controller;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.service.interfaces.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Validated
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<PaymentCardResponse> createPaymentCard(
            @Valid @RequestBody CreatePaymentCardRequest request) {

        PaymentCardResponse response = paymentCardService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> getPaymentCardById(
            @PathVariable UUID id) {

        PaymentCardResponse response = paymentCardService.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponse>> getAllPaymentCards(
            @RequestParam(required = false) String holder,
            Pageable pageable) {

        Page<PaymentCardResponse> response = paymentCardService.getAll(
                holder,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardResponse>> getPaymentCardsByUserId(
            @PathVariable UUID userId) {

        List<PaymentCardResponse> response =
                paymentCardService.getAllByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> updatePaymentCard(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentCardRequest request) {

        PaymentCardResponse response = paymentCardService.update(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activatePaymentCard(
            @PathVariable UUID id) {

        paymentCardService.updateActive(id, true);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePaymentCard(
            @PathVariable UUID id) {

        paymentCardService.updateActive(id, false);

        return ResponseEntity.ok().build();
    }

}