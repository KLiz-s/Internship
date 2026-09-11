package com.klyndyuk.orderservice.service;

import com.klyndyuk.orderservice.client.UserClient;
import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.orderservice.util.DateAndStatusRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.dto.response.UserResponse;
import com.klyndyuk.orderservice.entity.Item;
import com.klyndyuk.orderservice.entity.Order;
import com.klyndyuk.orderservice.entity.OrderItem;
import com.klyndyuk.orderservice.exception.AccessDeniedException;
import com.klyndyuk.orderservice.exception.ItemNotFoundException;
import com.klyndyuk.orderservice.exception.OrderNotFoundException;
import com.klyndyuk.orderservice.mapper.OrderItemMapper;
import com.klyndyuk.orderservice.mapper.OrderMapper;
import com.klyndyuk.orderservice.repository.ItemRepository;
import com.klyndyuk.orderservice.repository.OrderRepository;
import com.klyndyuk.orderservice.service.impl.OrderServiceImpl;
import com.klyndyuk.orderservice.util.TestConstants;
import com.klyndyuk.orderservice.util.TestItems;
import com.klyndyuk.orderservice.util.TestOrders;
import com.klyndyuk.orderservice.util.TestRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldReturnCreatedOrder() {
        CreateOrderRequest request = TestOrders.createOrderRequest();
        Order mappedOrder = TestOrders.createOrder();
        mappedOrder.setItems(new java.util.ArrayList<>());
        OrderResponse response = TestOrders.createOrderResponse();
        UserResponse userResponse = TestOrders.createUserResponse(TestConstants.USER_ID);

        Item firstItem = TestItems.createItem();
        Item secondItem = TestItems.createSecondItem();
        OrderItem firstOrderItem = new OrderItem();
        firstOrderItem.setQuantity(TestConstants.ITEM_QUANTITY);
        OrderItem secondOrderItem = new OrderItem();
        secondOrderItem.setQuantity(TestConstants.SECOND_ITEM_QUANTITY);

        given(userClient.getUser(TestConstants.USER_ID.toString()))
                .willReturn(userResponse);
        given(orderMapper.toEntity(request))
                .willReturn(mappedOrder);
        given(orderItemMapper.toEntity(request.getItems().getFirst()))
                .willReturn(firstOrderItem);
        given(orderItemMapper.toEntity(request.getItems().get(1)))
                .willReturn(secondOrderItem);
        given(itemRepository.findById(TestConstants.ITEM_ID))
                .willReturn(Optional.of(firstItem));
        given(itemRepository.findById(TestConstants.SECOND_ITEM_ID))
                .willReturn(Optional.of(secondItem));
        given(orderRepository.save(mappedOrder))
                .willReturn(mappedOrder);
        given(orderMapper.toResponse(mappedOrder))
                .willReturn(response);

        OrderResponse result = orderService.createOrder(
                request,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        );

        assertThat(result).isSameAs(response);
        assertThat(mappedOrder.getUserId()).isEqualTo(TestConstants.USER_ID);
        assertThat(mappedOrder.getTotalPrice()).isEqualByComparingTo(new BigDecimal("28.00"));

        then(orderMapper).should().toEntity(request);
        then(orderRepository).should().save(mappedOrder);
        then(orderMapper).should().toResponse(mappedOrder);
    }

    @Test
    void createOrder_shouldThrowWhenItemNotFound() {
        CreateOrderRequest request = TestOrders.createOrderRequest();
        Order mappedOrder = TestOrders.createOrder();
        mappedOrder.setItems(new java.util.ArrayList<>());

        given(userClient.getUser(TestConstants.USER_ID.toString()))
                .willReturn(TestOrders.createUserResponse(TestConstants.USER_ID));
        given(orderMapper.toEntity(request))
                .willReturn(mappedOrder);
        given(orderItemMapper.toEntity(request.getItems().getFirst()))
                .willReturn(new OrderItem());
        given(itemRepository.findById(TestConstants.ITEM_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(
                request,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(ItemNotFoundException.class);

        then(orderRepository).should(never()).save(any());
    }

    @Test
    void getById_shouldReturnOrder() {
        Order order = TestOrders.createOrder();
        OrderResponse response = TestOrders.createOrderResponse();
        UserResponse user = TestOrders.createUserResponse(order.getUserId());

        given(orderRepository.findByIdAndDeletedFalse(order.getId()))
                .willReturn(Optional.of(order));
        given(orderMapper.toResponse(order))
                .willReturn(response);
        given(userClient.getUser(order.getUserId().toString()))
                .willReturn(user);

        OrderResponse result = orderService.getById(
                order.getId(),
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        );

        assertThat(result).isSameAs(response);
        assertThat(result.getUser()).isSameAs(user);
    }

    @Test
    void getById_shouldThrowWhenOrderNotFound() {
        given(orderRepository.findByIdAndDeletedFalse(TestConstants.ORDER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(
                TestConstants.ORDER_ID,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_ADMIN)
        )).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getById_shouldThrowWhenAccessDenied() {
        Order order = TestOrders.createOrder(TestConstants.ORDER_ID, TestConstants.USER_ID);

        given(orderRepository.findByIdAndDeletedFalse(TestConstants.ORDER_ID))
                .willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getById(
                TestConstants.ORDER_ID,
                userDetails(TestConstants.SECOND_USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getAllByDateAndStatus_shouldReturnPage() {
        DateAndStatusRequest request = TestOrders.dateAndStatusRequest();
        Order order = TestOrders.createOrder();
        OrderResponse response = TestOrders.createOrderResponse();
        UserResponse user = TestOrders.createUserResponse(TestConstants.USER_ID);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        given(orderRepository.findAllByDeletedFalse(any(Specification.class), eq(pageable)))
                .willReturn(orderPage);
        given(orderMapper.toResponse(order))
                .willReturn(response);
        given(userClient.getUser(order.getUserId().toString()))
                .willReturn(user);

        Page<OrderResponse> result = orderService.getAllByDateAndStatus(request.getFromDate(), request.getToDate(), request.getStatus(), pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getContent().getFirst().getUser()).isSameAs(user);
    }

    @Test
    void getAllByUserId_shouldReturnOrdersForOwner() {
        UUID userId = TestConstants.USER_ID;
        Order order = TestOrders.createOrder();
        OrderResponse response = TestOrders.createOrderResponse();
        UserResponse user = TestOrders.createUserResponse(userId);

        given(orderRepository.findAllByUserIdAndDeletedFalse(userId))
                .willReturn(List.of(order));
        given(orderMapper.toResponse(order))
                .willReturn(response);
        given(userClient.getUser(userId.toString()))
                .willReturn(user);

        List<OrderResponse> result = orderService.getAllByUserId(
                userId,
                userDetails(userId, TestRole.ROLE_USER)
        );

        assertThat(result).containsExactly(response);
        assertThat(result.getFirst().getUser()).isSameAs(user);
    }

    @Test
    void getAllByUserId_shouldThrowWhenAccessDenied() {
        assertThatThrownBy(() -> orderService.getAllByUserId(
                TestConstants.USER_ID,
                userDetails(TestConstants.SECOND_USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(AccessDeniedException.class);

        then(orderRepository).shouldHaveNoInteractions();
    }

    @Test
    void update_shouldReturnUpdatedOrder() {
        UpdateOrderRequest request = TestOrders.updateOrderRequest();
        Order order = TestOrders.createOrder();
        OrderResponse response = TestOrders.createOrderResponse();
        UserResponse user = TestOrders.createUserResponse(order.getUserId());
        Item firstItem = TestItems.createItem();
        Item secondItem = TestItems.createSecondItem();
        OrderItem firstOrderItem = new OrderItem();
        firstOrderItem.setQuantity(1);
        OrderItem secondOrderItem = new OrderItem();
        secondOrderItem.setQuantity(3);

        given(orderRepository.findByIdAndDeletedFalse(order.getId()))
                .willReturn(Optional.of(order));
        given(orderItemMapper.toEntity(request.getItems().getFirst()))
                .willReturn(firstOrderItem);
        given(orderItemMapper.toEntity(request.getItems().get(1)))
                .willReturn(secondOrderItem);
        given(itemRepository.findById(TestConstants.ITEM_ID))
                .willReturn(Optional.of(firstItem));
        given(itemRepository.findById(TestConstants.SECOND_ITEM_ID))
                .willReturn(Optional.of(secondItem));
        given(orderRepository.save(order))
                .willReturn(order);
        given(orderMapper.toResponse(order))
                .willReturn(response);
        given(userClient.getUser(order.getUserId().toString()))
                .willReturn(user);

        OrderResponse result = orderService.update(
                order.getId(),
                request,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        );

        assertThat(result).isSameAs(response);
        assertThat(order.getTotalPrice()).isEqualByComparingTo(new BigDecimal("21.50"));

        then(orderMapper).should().updateEntityFromRequest(request, order);
        then(orderRepository).should().save(order);
    }

    @Test
    void update_shouldThrowWhenOrderNotFound() {
        given(orderRepository.findByIdAndDeletedFalse(TestConstants.ORDER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(
                TestConstants.ORDER_ID,
                TestOrders.updateOrderRequest(),
                userDetails(TestConstants.USER_ID, TestRole.ROLE_ADMIN)
        )).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void update_shouldThrowWhenAccessDenied() {
        Order order = TestOrders.createOrder(TestConstants.ORDER_ID, TestConstants.USER_ID);

        given(orderRepository.findByIdAndDeletedFalse(TestConstants.ORDER_ID))
                .willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.update(
                TestConstants.ORDER_ID,
                TestOrders.updateOrderRequest(),
                userDetails(TestConstants.SECOND_USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(AccessDeniedException.class);

        then(orderRepository).should(never()).save(any());
    }

    @Test
    void deleteById_shouldMarkOrderAsDeleted() {
        Order order = TestOrders.createOrder();

        given(orderRepository.findByIdAndDeletedFalse(order.getId()))
                .willReturn(Optional.of(order));

        orderService.deleteById(
                order.getId(),
                userDetails(order.getUserId(), TestRole.ROLE_USER)
        );

        assertThat(order.isDeleted()).isTrue();
    }

    @Test
    void deleteById_shouldThrowWhenOrderNotFound() {
        given(orderRepository.findByIdAndDeletedFalse(TestConstants.ORDER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteById(
                TestConstants.ORDER_ID,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_ADMIN)
        )).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getById_shouldThrowWhenOrderIsSoftDeleted() {
        UUID orderId = TestConstants.ORDER_ID;

        given(orderRepository.findByIdAndDeletedFalse(orderId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(
                orderId,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(OrderNotFoundException.class);

        then(userClient).shouldHaveNoInteractions();
    }

    @Test
    void update_shouldThrowWhenOrderIsSoftDeleted() {
        UUID orderId = TestConstants.ORDER_ID;

        given(orderRepository.findByIdAndDeletedFalse(orderId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(
                orderId,
                TestOrders.updateOrderRequest(),
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        )).isInstanceOf(OrderNotFoundException.class);

        then(orderRepository).should(never()).save(any());
        then(orderMapper).shouldHaveNoInteractions();
    }

    @Test
    void handlePaymentEvent_whenPaymentSuccessful_shouldSetOrderStatusToPaid() {
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setStatus("CREATED");

        CreatePaymentEvent event =
                new CreatePaymentEvent(
                        UUID.randomUUID(),
                        orderId,
                        "SUCCESS"
                );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        orderService.handlePaymentEvent(event);

        assertEquals("PAID", order.getStatus());

        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentEvent_whenPaymentFailed_shouldSetOrderStatusToPaymentFailed() {
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);
        order.setStatus("CREATED");

        CreatePaymentEvent event =
                new CreatePaymentEvent(
                        UUID.randomUUID(),
                        orderId,
                        "FAILED"
                );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        orderService.handlePaymentEvent(event);

        assertEquals("PAYMENT_FAILED", order.getStatus());

        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentEvent_whenOrderNotFound_shouldThrowException() {
        UUID orderId = UUID.randomUUID();

        CreatePaymentEvent event =
                new CreatePaymentEvent(
                        UUID.randomUUID(),
                        orderId,
                        "SUCCESS"
                );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.handlePaymentEvent(event)
        );

        verify(orderRepository, never()).save(any());
    }

    private UserDetails userDetails(UUID userId, TestRole role) {
        UserDetails userDetails = mock(UserDetails.class);
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role.name());

        lenient().when(userDetails.getUsername()).thenReturn(userId.toString());
        lenient().doReturn(List.of(authority)).when(userDetails).getAuthorities();

        return userDetails;
    }
}
