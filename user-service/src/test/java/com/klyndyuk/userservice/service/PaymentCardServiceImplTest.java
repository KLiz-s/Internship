package com.klyndyuk.userservice.service;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.exception.PaymentCardNotFoundException;
import com.klyndyuk.userservice.exception.UserNotFoundException;
import com.klyndyuk.userservice.mapper.PaymentCardMapper;
import com.klyndyuk.userservice.repository.PaymentCardRepository;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.service.impl.PaymentCardServiceImpl;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestPaymentCards;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Test
    void create_shouldReturnCreatedPaymentCard() {
        CreatePaymentCardRequest request = TestPaymentCards.createCreateRequest();
        User user = TestUsers.createUser();
        PaymentCard paymentCard = TestPaymentCards.createPaymentCardWithoutUser();
        PaymentCardResponse response = TestPaymentCards.createResponse();

        given(userRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.of(user));

        given(paymentCardMapper.toEntity(request))
                .willReturn(paymentCard);

        given(paymentCardRepository.save(paymentCard))
                .willReturn(paymentCard);

        given(paymentCardMapper.toResponse(paymentCard))
                .willReturn(response);

        PaymentCardResponse result = paymentCardService.create(request);

        assertThat(result).isSameAs(response);
        assertThat(paymentCard.getUser()).isSameAs(user);

        then(userRepository).should().findById(TestConstants.USER_ID);
        then(paymentCardMapper).should().toEntity(request);
        then(paymentCardRepository).should().save(paymentCard);
        then(paymentCardMapper).should().toResponse(paymentCard);
    }

    @Test
    void create_shouldThrowWhenUserNotFound() {
        CreatePaymentCardRequest request = TestPaymentCards.createCreateRequest();

        given(userRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardService.create(request))
                .isInstanceOf(UserNotFoundException.class);

        then(paymentCardMapper).shouldHaveNoInteractions();
    }

    @Test
    void getById_shouldReturnPaymentCard() {
        PaymentCard paymentCard = TestPaymentCards.createPaymentCard();
        PaymentCardResponse response = TestPaymentCards.createResponse();

        given(paymentCardRepository.findById(TestConstants.PAYMENT_CARD_ID))
                .willReturn(Optional.of(paymentCard));

        given(paymentCardMapper.toResponse(paymentCard))
                .willReturn(response);

        PaymentCardResponse result =
                paymentCardService.getById(TestConstants.PAYMENT_CARD_ID);

        assertThat(result).isSameAs(response);

        then(paymentCardRepository)
                .should()
                .findById(TestConstants.PAYMENT_CARD_ID);

        then(paymentCardMapper)
                .should()
                .toResponse(paymentCard);
    }

    @Test
    void getById_shouldThrowWhenPaymentCardNotFound() {
        given(paymentCardRepository.findById(TestConstants.PAYMENT_CARD_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentCardService.getById(TestConstants.PAYMENT_CARD_ID)
        ).isInstanceOf(PaymentCardNotFoundException.class);

        then(paymentCardMapper).shouldHaveNoInteractions();
    }

    @Test
    void getAll_shouldReturnPaymentCardsPage() {
        PaymentCard paymentCard = TestPaymentCards.createPaymentCard();
        PaymentCardResponse response = TestPaymentCards.createResponse();

        Page<PaymentCard> page = new PageImpl<>(List.of(paymentCard));
        PageRequest pageable = PageRequest.of(0, 10);

        given(paymentCardRepository.findAll(pageable))
                .willReturn(page);

        given(paymentCardMapper.toResponse(paymentCard))
                .willReturn(response);

        Page<PaymentCardResponse> result =
                paymentCardService.getAll(null, pageable);

        assertThat(result.getContent())
                .containsExactly(response);

        then(paymentCardRepository)
                .should()
                .findAll(pageable);
    }

    @Test
    void getAllByUserId_shouldReturnPaymentCards() {
        PaymentCard paymentCard = TestPaymentCards.createPaymentCard();
        PaymentCardResponse response = TestPaymentCards.createResponse();

        List<PaymentCard> list = List.of(paymentCard);

        given(userRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.of(TestUsers.createUser()));

        given(paymentCardRepository.findAllByUserId(TestConstants.USER_ID))
                .willReturn(list);

        given(paymentCardMapper.toResponse(paymentCard))
                .willReturn(response);

        List<PaymentCardResponse> result =
                paymentCardService.getAllByUserId(TestConstants.USER_ID);

        assertThat(result)
                .containsExactly(response);

        then(paymentCardRepository)
                .should()
                .findAllByUserId(TestConstants.USER_ID);
	then(userRepository)
                .should()
        	.findById(TestConstants.USER_ID);
    }

    @Test
    void update_shouldReturnUpdatedPaymentCard() {
        UpdatePaymentCardRequest request = TestPaymentCards.createUpdateRequest();
        PaymentCard paymentCard = TestPaymentCards.createPaymentCard();
        PaymentCardResponse response = TestPaymentCards.createResponse();

        given(paymentCardRepository.findById(TestConstants.PAYMENT_CARD_ID))
                .willReturn(Optional.of(paymentCard));

        given(paymentCardRepository.save(paymentCard))
                .willReturn(paymentCard);

        given(paymentCardMapper.toResponse(paymentCard))
                .willReturn(response);

        PaymentCardResponse result =
                paymentCardService.update(TestConstants.PAYMENT_CARD_ID, request);

        assertThat(result).isSameAs(response);

        then(paymentCardMapper)
                .should()
                .updateEntity(request, paymentCard);

        then(paymentCardRepository)
                .should()
                .save(paymentCard);
    }

    @Test
    void update_shouldThrowWhenPaymentCardNotFound() {
        given(paymentCardRepository.findById(TestConstants.PAYMENT_CARD_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentCardService.update(
                        TestConstants.PAYMENT_CARD_ID,
                        TestPaymentCards.createUpdateRequest()
                )
        ).isInstanceOf(PaymentCardNotFoundException.class);

        then(paymentCardMapper).shouldHaveNoInteractions();
    }

    @Test
    void updateActive_shouldUpdatePaymentCardStatus() {
        User user = TestUsers.createUser();
	PaymentCard paymentCard = TestPaymentCards.createPaymentCard(user);

	given(cacheManager.getCache("users"))
        	.willReturn(cache);
	
	given(paymentCardRepository.findById(TestConstants.PAYMENT_CARD_ID))
        	.willReturn(Optional.of(paymentCard));

        given(paymentCardRepository.updateActive(TestConstants.PAYMENT_CARD_ID, true))
		                .willReturn(1);
			
        paymentCardService.updateActive(TestConstants.PAYMENT_CARD_ID, true);
		
        then(paymentCardRepository)
                .should()
                .updateActive(TestConstants.PAYMENT_CARD_ID, true);
	then(paymentCardRepository)
        	.should()
        	.findById(TestConstants.PAYMENT_CARD_ID);
    }

    @Test
    void updateActive_shouldThrowWhenPaymentCardNotFound() {
        given(paymentCardRepository.updateActive(TestConstants.PAYMENT_CARD_ID, false))
                .willReturn(0);

        assertThatThrownBy(() ->
                paymentCardService.updateActive(TestConstants.PAYMENT_CARD_ID, false)
        ).isInstanceOf(PaymentCardNotFoundException.class);

        then(paymentCardRepository)
                .should()
                .updateActive(TestConstants.PAYMENT_CARD_ID, false);
    }
}
