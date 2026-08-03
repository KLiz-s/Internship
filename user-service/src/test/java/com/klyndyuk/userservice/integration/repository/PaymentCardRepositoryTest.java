package com.klyndyuk.userservice.integration.repository;

import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.repository.PaymentCardRepository;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestPaymentCards;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCardRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByHolder_shouldReturnCards() {
        User user = userRepository.save(TestUsers.createUser());

        paymentCardRepository.save(TestPaymentCards.createPaymentCard(user, "JOHN SMITH"));
        paymentCardRepository.save(TestPaymentCards.createPaymentCard(user, "JOHN SMITH"));

        Page<PaymentCard> page = paymentCardRepository.findAllByHolder(
                "JOHN SMITH",
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findAllByHolder_shouldReturnEmpty() {
        Page<PaymentCard> page = paymentCardRepository.findAllByHolder(
                "UNKNOWN",
                PageRequest.of(0, 10)
        );

        assertThat(page).isEmpty();
    }

    @Test
    void findAllByUserId_shouldReturnCards() {
        User user = userRepository.save(TestUsers.createUser());

        paymentCardRepository.save(TestPaymentCards.createPaymentCard(user, "ONE"));
        paymentCardRepository.save(TestPaymentCards.createPaymentCard(user, "TWO"));

        List<PaymentCard> result = paymentCardRepository.findAllByUserId(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllByUserId_shouldReturnEmpty() {
        List<PaymentCard> result = paymentCardRepository.findAllByUserId(
                TestConstants.USER_ID
        );

        assertThat(result).isEmpty();
    }

    @Test
    void updateActive_shouldUpdateStatus() {
        User user = userRepository.save(TestUsers.createUser());
        PaymentCard card = paymentCardRepository.save(TestPaymentCards.createPaymentCard(user));

        int updated = paymentCardRepository.updateActive(card.getId(), false);

        entityManager.flush();
        entityManager.clear();

        PaymentCard result = paymentCardRepository.findById(card.getId()).orElseThrow();

        assertThat(updated).isEqualTo(1);
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void updateActive_shouldReturnZeroWhenCardNotFound() {
        int updated = paymentCardRepository.updateActive(
                TestConstants.PAYMENT_CARD_ID,
                false
        );

        assertThat(updated).isZero();
    }
}