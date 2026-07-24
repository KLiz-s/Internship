package com.klyndyuk.userservice.integration.controller;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.repository.PaymentCardRepository;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestPaymentCards;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentCardControllerTest extends BaseControllerTest {

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_shouldReturnCreated() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        CreatePaymentCardRequest request = TestPaymentCards.createCreateRequest();
        request.setUserId(user.getId());

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.holder").value(request.getHolder()))
                .andExpect(jsonPath("$.number").value(request.getNumber()))
                .andExpect(jsonPath("$.expirationDate")
                        .value(request.getExpirationDate().toString()))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.count()).isEqualTo(1);

        PaymentCard card = paymentCardRepository.findAll().getFirst();

        assertThat(card.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void create_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        CreatePaymentCardRequest request = TestPaymentCards.createCreateRequest();
        request.setUserId(user.getId());
        request.setNumber("");

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertThat(paymentCardRepository.count()).isZero();
    }

    @Test
    void getById_shouldReturnPaymentCard() throws Exception {
        User user = userRepository.save(TestUsers.createUser());
        PaymentCard card = paymentCardRepository.save(
                TestPaymentCards.createPaymentCard(user)
        );

        mockMvc.perform(get("/cards/{id}", card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.holder").value(card.getHolder()))
                .andExpect(jsonPath("$.number").value(card.getNumber()))
                .andExpect(jsonPath("$.expirationDate")
                        .value(card.getExpirationDate().toString()))
                .andExpect(jsonPath("$.active").value(card.isActive()));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/cards/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());

        assertThat(paymentCardRepository.count()).isZero();
    }

    @Test
    void getAll_shouldReturnPaymentCards() throws Exception {
        User firstUser = userRepository.save(TestUsers.createUser());
        User secondUser = userRepository.save(TestUsers.createSecondUser());

        paymentCardRepository.save(TestPaymentCards.createPaymentCard(firstUser));
        PaymentCard secondCard = TestPaymentCards.createPaymentCard(secondUser);
        secondCard.setNumber(TestConstants.CARD_NUMBER_2);
        paymentCardRepository.save(secondCard);

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].holder").exists())
                .andExpect(jsonPath("$.content[0].number").exists())
                .andExpect(jsonPath("$.content[0].expirationDate").exists())
                .andExpect(jsonPath("$.content[0].active").value(true));
    }

    @Test
    void getAllByUserId_shouldReturnPaymentCards() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        PaymentCard firstCard = TestPaymentCards.createPaymentCard(user);
        PaymentCard secondCard = TestPaymentCards.createPaymentCard(user);
        secondCard.setNumber("5555666677778888");

        paymentCardRepository.save(firstCard);
        paymentCardRepository.save(secondCard);

        mockMvc.perform(get("/cards/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].holder").exists())
                .andExpect(jsonPath("$[0].number").exists())
                .andExpect(jsonPath("$[0].expirationDate").exists())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void update_shouldReturnUpdatedPaymentCard() throws Exception {
        User user = userRepository.save(TestUsers.createUser());
        PaymentCard card = paymentCardRepository.save(
                TestPaymentCards.createPaymentCard(user)
        );

        UpdatePaymentCardRequest request =
                TestPaymentCards.createUpdateRequest();
        request.setUserId(card.getUser().getId());

        mockMvc.perform(put("/cards/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId().toString()))
                .andExpect(jsonPath("$.holder").value(request.getHolder()))
                .andExpect(jsonPath("$.number").value(request.getNumber()))
                .andExpect(jsonPath("$.expirationDate")
                        .value(request.getExpirationDate().toString()))
                .andExpect(jsonPath("$.active").value(true));

        PaymentCard updated =
                paymentCardRepository.findById(card.getId()).orElseThrow();

        assertThat(updated.getHolder()).isEqualTo(request.getHolder());
        assertThat(updated.getNumber()).isEqualTo(request.getNumber());
        assertThat(updated.getExpirationDate())
                .isEqualTo(request.getExpirationDate());
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        UpdatePaymentCardRequest request =
                TestPaymentCards.createUpdateRequest();

        request.setUserId(user.getId());

        mockMvc.perform(put("/cards/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());

        assertThat(paymentCardRepository.count()).isZero();
    }

    @Test
    void activate_shouldUpdatePaymentCardStatus() throws Exception {
        User user = userRepository.save(TestUsers.createUser());
        PaymentCard card = paymentCardRepository.save(
                TestPaymentCards.createPaymentCard(user)
        );
        card.setActive(false);
        card = paymentCardRepository.save(card);

        mockMvc.perform(patch("/cards/{id}/activate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        PaymentCard updated = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertThat(updated.isActive()).isTrue();
    }

    @Test
    void deactivate_shouldUpdatePaymentCardStatus() throws Exception {
        User user = userRepository.save(TestUsers.createUser());
        PaymentCard card = paymentCardRepository.save(
                TestPaymentCards.createPaymentCard(user)
        );

        mockMvc.perform(patch("/cards/{id}/deactivate", card.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        PaymentCard updated = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void activate_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/cards/{id}/activate", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deactivate_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/cards/{id}/deactivate", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_shouldReturnConflictWhenUserHasMaximumCards() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        for (int i = 0; i < 5; i++) {
            PaymentCard card = TestPaymentCards.createPaymentCard(user);
            card.setNumber("411111111111111" + i);
            paymentCardRepository.save(card);
        }

        CreatePaymentCardRequest request = TestPaymentCards.createCreateRequest();
        request.setUserId(user.getId());
        request.setNumber("5555555555555555");

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());

        assertThat(paymentCardRepository.count()).isEqualTo(5);
    }

    @Test
    void getById_shouldReturnUserWithPaymentCards() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        PaymentCard first = TestPaymentCards.createPaymentCard(user);
        first.setNumber("4111111111111111");

        PaymentCard second = TestPaymentCards.createPaymentCard(user);
        second.setNumber("5555555555554444");

        paymentCardRepository.save(first);
        paymentCardRepository.save(second);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.paymentCards.length()").value(2));
    }

    @Test
    void update_shouldMoveCardToAnotherUser() throws Exception {
        User firstUser = userRepository.save(TestUsers.createUser());
        User secondUser = userRepository.save(TestUsers.createSecondUser());

        PaymentCard card = paymentCardRepository.save(
                TestPaymentCards.createPaymentCard(firstUser)
        );

        UpdatePaymentCardRequest request =
                TestPaymentCards.createUpdateRequest();
        request.setUserId(secondUser.getId());

        mockMvc.perform(put("/cards/{id}", card.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        PaymentCard updated = paymentCardRepository.findById(card.getId())
                .orElseThrow();

        assertThat(updated.getUser().getId()).isEqualTo(secondUser.getId());

        User oldUser = userRepository.findWithPaymentCardsById(firstUser.getId())
                .orElseThrow();

        User newUser = userRepository.findWithPaymentCardsById(secondUser.getId())
                .orElseThrow();

        assertThat(oldUser.getPaymentCards()).isEmpty();
        assertThat(newUser.getPaymentCards())
                .extracting(PaymentCard::getId)
                .containsExactly(card.getId());
    }
}