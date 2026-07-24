package com.klyndyuk.userservice.util;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

public final class TestPaymentCards {
    private static final AtomicInteger CARD_SEQUENCE = new AtomicInteger();

    private TestPaymentCards() {
    }

    public static PaymentCard createPaymentCardWithoutUser() {
        PaymentCard paymentCard = new PaymentCard();

        paymentCard.setNumber(TestConstants.CARD_NUMBER);
        paymentCard.setHolder(TestConstants.CARD_HOLDER);
        paymentCard.setExpirationDate(TestConstants.CARD_EXPIRATION_DATE);
        paymentCard.setActive(true);

        return paymentCard;
    }     

    public static PaymentCard createPaymentCard() {
        return createPaymentCardWithoutUser();
    }

    public static PaymentCard createPaymentCard(UUID id) {
        PaymentCard paymentCard = createPaymentCard();
        paymentCard.setId(id);
        return paymentCard;
    }

    public static PaymentCard createPaymentCard(User user) {
        PaymentCard paymentCard = createPaymentCardWithoutUser();
        paymentCard.setUser(user);
        return paymentCard;
    }

    public static PaymentCard createPaymentCard(UUID id, User user) {
        PaymentCard paymentCard = createPaymentCard(user);
        paymentCard.setId(id);
        return paymentCard;
    }

    public static PaymentCard createPaymentCard(UUID id, User user, String holder) {
        PaymentCard paymentCard = createPaymentCard(id, user);
        paymentCard.setHolder(holder);
        return paymentCard;
    }

    public static PaymentCard createSecondPaymentCard() {
        PaymentCard paymentCard = createPaymentCardWithoutUser();
        paymentCard.setId(TestConstants.PAYMENT_CARD_ID_2);
        paymentCard.setNumber(TestConstants.CARD_NUMBER_2);
        paymentCard.setHolder(TestConstants.CARD_HOLDER_2);
        paymentCard.setExpirationDate(TestConstants.CARD_EXPIRATION_DATE_2);

        return paymentCard;
    }

    public static CreatePaymentCardRequest createCreateRequest() {
        CreatePaymentCardRequest request = new CreatePaymentCardRequest();

        request.setUserId(TestConstants.USER_ID);
        request.setNumber(TestConstants.CARD_NUMBER);
        request.setHolder(TestConstants.CARD_HOLDER);
        request.setExpirationDate(TestConstants.CARD_EXPIRATION_DATE);

        return request;
    }

    public static CreatePaymentCardRequest createCreateRequest(UUID userId) {
        CreatePaymentCardRequest request = createCreateRequest();
        request.setUserId(userId);
        return request;
    }

    public static UpdatePaymentCardRequest createUpdateRequest() {
        UpdatePaymentCardRequest request = new UpdatePaymentCardRequest();
        request.setNumber(TestConstants.CARD_NUMBER);
        request.setHolder(TestConstants.UPDATED_CARD_HOLDER);
        request.setExpirationDate(TestConstants.CARD_EXPIRATION_DATE);

        return request;
    }

    public static PaymentCardResponse createResponse() {
        PaymentCardResponse response = new PaymentCardResponse();

        response.setId(TestConstants.PAYMENT_CARD_ID);
        response.setNumber(TestConstants.CARD_NUMBER);
        response.setHolder(TestConstants.CARD_HOLDER);
        response.setExpirationDate(TestConstants.CARD_EXPIRATION_DATE);
        response.setActive(true);

        return response;
    }

    public static PaymentCardResponse createResponse(UUID id) {
        PaymentCardResponse response = createResponse();
        response.setId(id);
        return response;
    }                                                                                                 

    public static PaymentCard createPaymentCard(String holder) {
        PaymentCard paymentCard = createPaymentCard();
        paymentCard.setHolder(holder);
        return paymentCard;
    }

    public static PaymentCard createPaymentCard(User user,
                                                String holder) {
        PaymentCard paymentCard = createPaymentCard(user);
        paymentCard.setHolder(holder);
        paymentCard.setNumber(String.format(
                "4111111111111%03d",
                CARD_SEQUENCE.incrementAndGet()
        ));
        return paymentCard;
    }
}
