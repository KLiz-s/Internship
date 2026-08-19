package com.klyndyuk.orderservice.integration.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.util.DateAndStatusRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.UserResponse;
import com.klyndyuk.orderservice.entity.Item;
import com.klyndyuk.orderservice.entity.Order;
import com.klyndyuk.orderservice.repository.ItemRepository;
import com.klyndyuk.orderservice.repository.OrderRepository;
import com.klyndyuk.orderservice.util.TestConstants;
import com.klyndyuk.orderservice.util.TestItems;
import com.klyndyuk.orderservice.util.TestOrders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

class OrderControllerTest extends BaseControllerTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static final WireMockServer wireMockServer =
            new WireMockServer(wireMockConfig().dynamicPort());

    static {
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "user-service.url",
                () -> "http://localhost:" + wireMockServer.port()
        );
    }

    @Test
    void createOrder_shouldReturnCreated() throws Exception {
        Item firstItem = itemRepository.save(TestItems.createItemForPersist());
        Item secondItem = itemRepository.save(TestItems.createSecondItemForPersist());
        CreateOrderRequest request = TestOrders.createOrderRequest();
        request.getItems().getFirst().setItemId(firstItem.getId());
        request.getItems().get(1).setItemId(secondItem.getId());
        UserResponse userResponse = TestOrders.createUserResponse(TestConstants.USER_ID);

        stubUser(TestConstants.USER_ID);

        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(TestConstants.USER_ID.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalPrice").value(28.00))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.user.id").value(TestConstants.USER_ID.toString()));

        assertThat(itemRepository.findById(firstItem.getId())).isPresent();
        assertThat(itemRepository.findById(secondItem.getId())).isPresent();
        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void createOrder_shouldReturnNotFoundWhenItemMissing() throws Exception {
        CreateOrderRequest request = TestOrders.createOrderRequest();
        UserResponse userResponse = TestOrders.createUserResponse(TestConstants.USER_ID);

        stubUser(TestConstants.USER_ID);

        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        Item item = itemRepository.save(TestItems.createItemForPersist());
        Order order = TestOrders.withSingleItem(TestOrders.createOrderForPersist(), item, 2);
        order = orderRepository.save(order);

        stubUser(order.getUserId());

        mockMvc.perform(get("/api/orders/{id}", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.userId").value(order.getUserId().toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.user.id").value(order.getUserId().toString()));
    }

    @Test
    void getAllByDateAndStatus_shouldReturnOrders() throws Exception {
        Item item = itemRepository.save(TestItems.createItemForPersist());

        Order first = TestOrders.withSingleItem(
                TestOrders.createOrderForPersist(), item, 1);
        first.setStatus("CREATED");
        first = orderRepository.save(first);

        Order second = TestOrders.withSingleItem(
                TestOrders.createSecondOrderForPersist(), item, 1);
        second.setStatus("SHIPPED");
        second = orderRepository.save(second);

        stubUser(first.getUserId());

        Instant fromDate = first.getCreatedAt().minusSeconds(1);
        Instant toDate = first.getCreatedAt().plusSeconds(1);

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, adminBearerToken())
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @Test
    void getAllByUserId_shouldReturnOrdersForUser() throws Exception {
        Item item = itemRepository.save(TestItems.createItemForPersist());
        Order order = TestOrders.withSingleItem(TestOrders.createOrderForPersist(), item, 1);
        order = orderRepository.save(order);

        stubUser(order.getUserId());

        mockMvc.perform(get("/api/orders/user/{userId}", TestConstants.USER_ID)
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(order.getId().toString()));
    }

    @Test
    void updateOrder_shouldReturnUpdatedOrder() throws Exception {
        Item firstItem = itemRepository.save(TestItems.createItemForPersist());
        Item secondItem = itemRepository.save(TestItems.createSecondItemForPersist());
        Order order = TestOrders.withSingleItem(TestOrders.createOrderForPersist(), firstItem, 1);
        order = orderRepository.save(order);
        UpdateOrderRequest request = TestOrders.updateOrderRequest();
        request.getItems().getFirst().setItemId(firstItem.getId());
        request.getItems().get(1).setItemId(secondItem.getId());

        stubUser(order.getUserId());

        mockMvc.perform(put("/api/orders/{id}", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalPrice").value(21.50));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated.getTotalPrice()).isEqualByComparingTo("21.50");
        assertThat(itemRepository.findById(firstItem.getId())).isPresent();
        assertThat(itemRepository.findById(secondItem.getId())).isPresent();
    }

    @Test
    void deleteOrder_shouldSoftDeleteOrder() throws Exception {
        Item item = itemRepository.save(TestItems.createItemForPersist());
        Order order = TestOrders.withSingleItem(TestOrders.createOrderForPersist(), item, 1);
        order = orderRepository.save(order);

        mockMvc.perform(delete("/api/orders/{id}", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken()))
                .andExpect(status().isNoContent());

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
    }

    @Test
    void getAllByDateAndStatus_shouldReturnForbiddenForUserRole() throws Exception {
        DateAndStatusRequest request = new DateAndStatusRequest();
        request.setFromDate(Instant.parse("2026-01-10T00:00:00Z"));
        request.setToDate(Instant.parse("2026-01-10T23:59:59Z"));
        request.setStatus("CREATED");

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .param("fromDate", request.getFromDate().toString())
                        .param("toDate", request.getToDate().toString())
                        .param("status", request.getStatus()))
                .andExpect(status().isForbidden());
    }

    private void stubUser(UUID userId) throws Exception {
        UserResponse userResponse = TestOrders.createUserResponse(userId);

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo("/api/users/" + userId))
                        .willReturn(
                                WireMock.okJson(
                                        objectMapper.writeValueAsString(userResponse)
                                )
                        )
        );
    }
}
