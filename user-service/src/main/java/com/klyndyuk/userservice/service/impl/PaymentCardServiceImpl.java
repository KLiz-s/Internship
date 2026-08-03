package com.klyndyuk.userservice.service.impl;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.exception.PaymentCardNotFoundException;
import com.klyndyuk.userservice.exception.UserHasMaximumCardsException;
import com.klyndyuk.userservice.exception.UserNotFoundException;
import com.klyndyuk.userservice.mapper.PaymentCardMapper;
import com.klyndyuk.userservice.repository.PaymentCardRepository;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.service.interfaces.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {
    private static final int MAX_PAYMENT_CARDS = 5;

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    @Override
    @CacheEvict(value = "users", key = "#request.userId")
    public PaymentCardResponse create(CreatePaymentCardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        if (paymentCardRepository.findAllByUserId(user.getId()).size() >= MAX_PAYMENT_CARDS) {
            throw new UserHasMaximumCardsException(user.getId());
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(request);
        paymentCard.setUser(user);

        PaymentCard savedPaymentCard = paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toResponse(savedPaymentCard);
    }

    @Override
    public PaymentCardResponse getById(UUID id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        return paymentCardMapper.toResponse(paymentCard);
    }

    @Override
    public Page<PaymentCardResponse> getAll(String holder,
                                            Pageable pageable) {
        Page<PaymentCard> paymentCards;

        if (holder == null || holder.isBlank()) {
            paymentCards = paymentCardRepository.findAll(pageable);
        } else {
            paymentCards = paymentCardRepository.findAllByHolder(holder, pageable);
        }

        return paymentCards.map(paymentCardMapper::toResponse);
    }

    @Override
    public List<PaymentCardResponse> getAllByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return paymentCardRepository.findAllByUserId(userId)
                .stream()
                .map(paymentCardMapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "users", key = "#request.userId")
    @Transactional
    public PaymentCardResponse update(UUID id,
                                      UpdatePaymentCardRequest request) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        paymentCardMapper.updateEntity(request, paymentCard);

        PaymentCard updatedPaymentCard = paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toResponse(updatedPaymentCard);
    }

    @Override
    @Transactional
    public void updateActive(UUID id, boolean active) {
        int updated = paymentCardRepository.updateActive(id, active);

        if (updated == 0) {
            throw new PaymentCardNotFoundException(id);
        }

        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        Objects.requireNonNull(cacheManager
                        .getCache("users"))
                .evict(paymentCard.getUser().getId());

    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        User user = paymentCard.getUser();

        user.removePaymentCard(paymentCard);
        paymentCardRepository.delete(paymentCard);

        Objects.requireNonNull(cacheManager
                        .getCache("users"))
                .evict(user.getId());
    }
}