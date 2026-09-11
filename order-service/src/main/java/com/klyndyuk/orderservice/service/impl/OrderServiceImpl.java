package com.klyndyuk.orderservice.service.impl;

import com.klyndyuk.orderservice.client.UserClient;
import com.klyndyuk.orderservice.dto.request.CreateOrderItemRequest;
import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.dto.response.UserResponse;
import com.klyndyuk.orderservice.entity.Order;
import com.klyndyuk.orderservice.entity.OrderItem;
import com.klyndyuk.orderservice.exception.AccessDeniedException;
import com.klyndyuk.orderservice.exception.ItemNotFoundException;
import com.klyndyuk.orderservice.exception.OrderNotFoundException;
import com.klyndyuk.orderservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.orderservice.mapper.OrderItemMapper;
import com.klyndyuk.orderservice.mapper.OrderMapper;
import com.klyndyuk.orderservice.repository.ItemRepository;
import com.klyndyuk.orderservice.repository.OrderRepository;
import com.klyndyuk.orderservice.service.interfaces.OrderService;
import com.klyndyuk.orderservice.specification.OrderSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest, UserDetails userDetails) {
        UserResponse userResponse = getUserById(userDetails.getUsername());
        Order order = orderMapper.toEntity(createOrderRequest);
        order.setUserId(userResponse.getId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CreateOrderItemRequest itemRequest : createOrderRequest.getItems()) {
            OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
            order.addItem(orderItem);
            orderItem.setOrder(order);
            orderItem.setItem(itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException(itemRequest.getItemId())));
            totalPrice = totalPrice.add(orderItem.getItem().getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        }
        order.setTotalPrice(totalPrice);

        order = orderRepository.save(order);
        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setUser(userResponse);
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse getById(UUID id, UserDetails userDetails) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (canInteractWithOrder(order, userDetails)) {
            UserResponse userResponse = getUserById(order.getUserId().toString());
            OrderResponse orderResponse = orderMapper.toResponse(order);
            orderResponse.setUser(userResponse);
            return orderResponse;
        }
        else {
            throw new AccessDeniedException();
        }
    }

    @Override
    @Transactional
    public Page<OrderResponse> getAllByDateAndStatus(Instant fromDate, Instant toDate, String status, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.byFilters(fromDate, toDate, status);
        return orderRepository.findAllByDeletedFalse(specification, pageable).map(order -> {
            OrderResponse orderResponse = orderMapper.toResponse(order);
            UserResponse userResponse=userClient.getUser(order.getUserId().toString());
            orderResponse.setUser(userResponse); return orderResponse;});
    }

    @Override
    @Transactional
    public List<OrderResponse> getAllByUserId(UUID userId, UserDetails userDetails) {
        if (userDetails.getUsername().equals(userId.toString()) || userDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"))) {
            List<Order> orders = orderRepository.findAllByUserIdAndDeletedFalse(userId);
            UserResponse userResponse = getUserById(userId.toString());
            return orders.stream()
                    .map(orderMapper::toResponse)
                    .peek(orderResponse -> orderResponse.setUser(userResponse))
                    .toList();
        } else  {
            throw new AccessDeniedException();
        }
    }

    @Transactional
    @Override
    public OrderResponse update(UUID id, UpdateOrderRequest updateOrderRequest, UserDetails userDetails) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if(canInteractWithOrder(order, userDetails)) {
            orderMapper.updateEntityFromRequest(updateOrderRequest, order);
            if (updateOrderRequest.getItems() != null) {
                BigDecimal totalPrice = BigDecimal.ZERO;
                if (order.getItems() == null) {
                    order.setItems(new ArrayList<>());
                } else {
                    order.getItems().clear();
                }
                for (CreateOrderItemRequest itemRequest : updateOrderRequest.getItems()) {
                    OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
                    order.addItem(orderItem);
                    orderItem.setOrder(order);
                    orderItem.setItem(itemRepository.findById(itemRequest.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException(itemRequest.getItemId())));
                    totalPrice = totalPrice.add(orderItem.getItem().getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
                }
                order.setTotalPrice(totalPrice);
            }
            order = orderRepository.save(order);
            OrderResponse orderResponse = orderMapper.toResponse(order);
            UserResponse userResponse = getUserById(order.getUserId().toString());
            orderResponse.setUser(userResponse);
            return orderResponse;
        }
        throw new AccessDeniedException();
    }

    @Override
    @Transactional
    public void deleteById(UUID id, UserDetails userDetails) {
        Order order = orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (canInteractWithOrder(order, userDetails)) {
            order.setDeleted(true);
        } else {
            throw new AccessDeniedException();
        }
    }

    @Override
    @Transactional
    public void handlePaymentEvent(CreatePaymentEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if (event.status().equals("SUCCESS")) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }

        orderRepository.save(order);
    }

    private UserResponse getUserById(String userId) {
        return userClient.getUser(userId);
    }

    private boolean canInteractWithOrder(Order order, UserDetails userDetails) {
        return order.getUserId().toString().equals(userDetails.getUsername()) || userDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"));
    }
}
