package com.klyndyuk.userservice.integration.repository;

import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.specification.UserSpecification;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestPaymentCards;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findWithPaymentCardsById_shouldReturnUserWithPaymentCards() {
        User user = TestUsers.createUser();
        user.addPaymentCard(TestPaymentCards.createPaymentCardWithoutUser());

        userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        User result = userRepository.findWithPaymentCardsById(user.getId())
                .orElseThrow();

        assertThat(result.getPaymentCards())
                .singleElement()
                .satisfies(paymentCard -> {
                    assertThat(paymentCard.getNumber()).isEqualTo(TestConstants.CARD_NUMBER);
                    assertThat(paymentCard.getHolder()).isEqualTo(TestConstants.CARD_HOLDER);
                    assertThat(paymentCard.getExpirationDate()).isEqualTo(TestConstants.CARD_EXPIRATION_DATE);
                    assertThat(paymentCard.isActive()).isTrue();
                    assertThat(paymentCard.getUser().getId()).isEqualTo(user.getId());
                });
    }

    @Test
    void findWithPaymentCardsById_shouldReturnEmptyWhenUserNotFound() {
        assertThat(userRepository.findWithPaymentCardsById(TestUsers.createSecondUser().getId()))
                .isEmpty();
    }

    @Test
    void updateActive_shouldUpdateStatus() {
        User user = userRepository.save(TestUsers.createUser());

        int updated = userRepository.updateActive(user.getId(), false);

        entityManager.flush();
        entityManager.clear();

        User result = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated).isEqualTo(1);
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void updateActive_shouldReturnZeroWhenUserNotFound() {
        int updated = userRepository.updateActive(TestUsers.createSecondUser().getId(), false);

        assertThat(updated).isZero();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.save(TestUsers.createUser());
        userRepository.save(TestUsers.createSecondUser());

        Page<User> page = userRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findAll_shouldFilterByName() {
        userRepository.save(TestUsers.createUser("John", "Smith"));
        userRepository.save(TestUsers.createUser("Jane", "Smith"));
        userRepository.save(TestUsers.createUser("John", "Doe"));

        Page<User> page = userRepository.findAll(
                UserSpecification.byFilters("John", null),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .extracting(User::getName)
                .containsExactlyInAnyOrder("John", "John");
    }

    @Test
    void findAll_shouldFilterBySurname() {
        userRepository.save(TestUsers.createUser("John", "Smith"));
        userRepository.save(TestUsers.createUser("Jane", "Smith"));
        userRepository.save(TestUsers.createUser("John", "Doe"));

        Page<User> page = userRepository.findAll(
                UserSpecification.byFilters(null, "Smith"),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .extracting(User::getSurname)
                .containsExactlyInAnyOrder("Smith", "Smith");
    }

    @Test
    void findAll_shouldFilterByNameAndSurname() {
        userRepository.save(TestUsers.createUser("John", "Smith"));
        userRepository.save(TestUsers.createUser("John", "Doe"));
        userRepository.save(TestUsers.createUser("Jane", "Smith"));

        Page<User> page = userRepository.findAll(
                UserSpecification.byFilters("John", "Smith"),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .singleElement()
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("John");
                    assertThat(user.getSurname()).isEqualTo("Smith");
                });
    }

    @Test
    void findAll_shouldReturnPagedResult() {
        userRepository.save(TestUsers.createUser("User1"));
        userRepository.save(TestUsers.createUser("User2"));
        userRepository.save(TestUsers.createUser("User3"));

        Page<User> page = userRepository.findAll(PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}