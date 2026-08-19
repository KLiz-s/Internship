package com.klyndyuk.orderservice.util;

import com.klyndyuk.orderservice.dto.request.CreateOrderItemRequest;
import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderItemResponse;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.dto.response.UserResponse;
import com.klyndyuk.orderservice.entity.Item;
import com.klyndyuk.orderservice.entity.Order;
import com.klyndyuk.orderservice.entity.OrderItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TestOrders {
    private TestOrders() {
    }

    public static CreateOrderRequest createOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(List.of(
                createOrderItemRequest(TestConstants.ITEM_ID, TestConstants.ITEM_QUANTITY),
                createOrderItemRequest(TestConstants.SECOND_ITEM_ID, TestConstants.SECOND_ITEM_QUANTITY)
        ));
        return request;
    }

    public static UpdateOrderRequest updateOrderRequest() {
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setItems(List.of(
                createOrderItemRequest(TestConstants.ITEM_ID, 1),
                createOrderItemRequest(TestConstants.SECOND_ITEM_ID, 3)
        ));
        return request;
    }

    public static DateAndStatusRequest dateAndStatusRequest() {
        DateAndStatusRequest request = new DateAndStatusRequest();
        request.setFromDate(TestConstants.ORDER_CREATED_AT.minusSeconds(3600));
        request.setToDate(TestConstants.ORDER_UPDATED_AT.plusSeconds(3600));
        request.setStatus(TestConstants.ORDER_STATUS);
        return request;
    }

    public static Order createOrder() {
        return createOrder(TestConstants.ORDER_ID, TestConstants.USER_ID);
    }

    public static Order createSecondOrder() {
        return createOrder(TestConstants.SECOND_ORDER_ID, TestConstants.SECOND_USER_ID);
    }

    public static Order createOrder(UUID orderId, UUID userId) {
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(TestConstants.ORDER_STATUS);
        order.setTotalPrice(TestConstants.TOTAL_PRICE);
        order.setDeleted(false);
        order.setCreatedAt(TestConstants.ORDER_CREATED_AT);
        order.setUpdatedAt(TestConstants.ORDER_UPDATED_AT);
        order.setItems(new ArrayList<>());
        return order;
    }

    public static Order createOrderForPersist() {
        Order order = createOrder(null, TestConstants.USER_ID);
        order.setUpdatedAt(TestConstants.ORDER_UPDATED_AT);
        return order;
    }

    public static Order createSecondOrderForPersist() {
        Order order = createOrder(null, TestConstants.SECOND_USER_ID);
        order.setUpdatedAt(TestConstants.ORDER_UPDATED_AT);
        return order;
    }

    public static Order withSingleItem(Order order, Item item, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        order.addItem(orderItem);
        return order;
    }

    public static OrderResponse createOrderResponse() {
        OrderResponse response = new OrderResponse();
        response.setId(TestConstants.ORDER_ID);
        response.setUserId(TestConstants.USER_ID);
        response.setStatus(TestConstants.ORDER_STATUS);
        response.setTotalPrice(TestConstants.TOTAL_PRICE);
        response.setItems(List.of(createOrderItemResponse()));
        response.setUser(createUserResponse(TestConstants.USER_ID));
        return response;
    }

    public static OrderItemResponse createOrderItemResponse() {
        OrderItemResponse response = new OrderItemResponse();
        response.setName(TestConstants.ITEM_NAME);
        response.setPrice(TestConstants.ITEM_PRICE);
        response.setQuantity(TestConstants.ITEM_QUANTITY);
        return response;
    }

    public static UserResponse createUserResponse(UUID userId) {
        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("John");
        response.setSurname("Doe");
        response.setEmail("john.doe@example.com");
        response.setBirthDate(LocalDate.of(1990, 1, 1));
        response.setActive(true);
        return response;
    }

    private static CreateOrderItemRequest createOrderItemRequest(UUID itemId, Integer quantity) {
        CreateOrderItemRequest request = new CreateOrderItemRequest();
        request.setItemId(itemId);
        request.setQuantity(quantity);
        return request;
    }
}
