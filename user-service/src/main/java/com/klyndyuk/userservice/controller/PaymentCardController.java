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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Validated
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<PaymentCardResponse> createPaymentCard(
            @Valid @RequestBody CreatePaymentCardRequest request, @AuthenticationPrincipal UserDetails userDetails) {

        PaymentCardResponse response = paymentCardService.create(request,  userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> getPaymentCardById(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        PaymentCardResponse response = paymentCardService.getById(id,  userDetails);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
            @PathVariable UUID userId, @AuthenticationPrincipal UserDetails userDetails) {

        List<PaymentCardResponse> response =
                paymentCardService.getAllByUserId(userId, userDetails);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> updatePaymentCard(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentCardRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentCardResponse response = paymentCardService.update(id, request, userDetails);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activatePaymentCard(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        paymentCardService.updateActive(id, true, userDetails);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePaymentCard(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        paymentCardService.updateActive(id, false, userDetails);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentCard(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        paymentCardService.delete(id, userDetails);

        return ResponseEntity.noContent().build();
    }
}