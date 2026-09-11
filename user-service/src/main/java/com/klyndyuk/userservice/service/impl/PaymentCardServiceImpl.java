package com.klyndyuk.userservice.service.impl;

import com.klyndyuk.userservice.dto.request.CreatePaymentCardRequest;
import com.klyndyuk.userservice.dto.request.UpdatePaymentCardRequest;
import com.klyndyuk.userservice.dto.response.PaymentCardResponse;
import com.klyndyuk.userservice.entity.PaymentCard;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.exception.AccessDeniedException;
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
import org.springframework.security.core.userdetails.UserDetails;
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
    @CacheEvict(
            value = "users",
            key = "T(java.util.UUID).fromString(#userDetails.username)"
    )
    @Transactional
    public PaymentCardResponse create(CreatePaymentCardRequest request, UserDetails userDetails) {
        User user = userRepository.findById(UUID.fromString(userDetails.getUsername()))
                .orElseThrow(() -> new UserNotFoundException(UUID.fromString(userDetails.getUsername())));

        if (paymentCardRepository.findAllByUserId(user.getId()).size() >= MAX_PAYMENT_CARDS) {
            throw new UserHasMaximumCardsException(user.getId());
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(request);
        user.addPaymentCard(paymentCard);

        PaymentCard savedPaymentCard = paymentCardRepository.save(paymentCard);

        return paymentCardMapper.toResponse(savedPaymentCard);
    }

    @Override
    public PaymentCardResponse getById(UUID id, UserDetails userDetails) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        if (canInteractWithPaymentCard(paymentCard, userDetails)) {
            return paymentCardMapper.toResponse(paymentCard);
        }  else {
            throw new AccessDeniedException();
        }
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
    public List<PaymentCardResponse> getAllByUserId(UUID userId, UserDetails userDetails) {
        if (userDetails.getUsername().equals(userId.toString()) || userDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"))) {
            return paymentCardRepository.findAllByUserId(userId)
                    .stream()
                    .map(paymentCardMapper::toResponse)
                    .toList();
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    @CacheEvict(value = "users", key = "#request.userId")
    @Transactional
    public PaymentCardResponse update(UUID id,
                                      UpdatePaymentCardRequest request, UserDetails userDetails) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        if (canInteractWithPaymentCard(paymentCard, userDetails)) {
            User currentUser = paymentCard.getUser();

            boolean userNeedUpdate = false;

            if (request.getUserId() != null
                    && !request.getUserId().equals(currentUser.getId())) {
                userNeedUpdate = true;
            }

            paymentCardMapper.updateEntity(request, paymentCard);

            if (userNeedUpdate) {
                User newUser = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

                if (paymentCardRepository.findAllByUserId(newUser.getId()).size() >= MAX_PAYMENT_CARDS) {
                    throw new UserHasMaximumCardsException(newUser.getId());
                }

                paymentCard.setUser(newUser);
                newUser.addPaymentCard(paymentCard);
            }

            PaymentCard updatedPaymentCard = paymentCardRepository.save(paymentCard);

            return paymentCardMapper.toResponse(updatedPaymentCard);
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    @Transactional
    public void updateActive(UUID id, boolean active, UserDetails userDetails) {
        int updated = paymentCardRepository.updateActive(id, active);

        if (updated == 0) {
            throw new PaymentCardNotFoundException(id);
        }

        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        if (canInteractWithPaymentCard(paymentCard, userDetails)) {
            Objects.requireNonNull(cacheManager
                            .getCache("users"))
                    .evict(paymentCard.getUser().getId());
        } else {
            throw new AccessDeniedException();
        }

    }

    @Override
    @Transactional
    public void delete(UUID id,  UserDetails userDetails) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        if (canInteractWithPaymentCard(paymentCard, userDetails)) {
            User user = paymentCard.getUser();

            user.removePaymentCard(paymentCard);
            paymentCardRepository.delete(paymentCard);

            Objects.requireNonNull(cacheManager
                            .getCache("users"))
                    .evict(user.getId());
        } else  {
            throw new AccessDeniedException();
        }
    }


    private boolean canInteractWithPaymentCard(PaymentCard paymentCard, UserDetails userDetails) {
        return paymentCard.getUser().getId().toString().equals(userDetails.getUsername()) || userDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"));
    }
}
