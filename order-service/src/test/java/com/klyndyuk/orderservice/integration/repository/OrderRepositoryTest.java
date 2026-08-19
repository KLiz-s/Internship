package com.klyndyuk.orderservice.integration.repository;

import com.klyndyuk.orderservice.entity.Item;
import com.klyndyuk.orderservice.entity.Order;
import com.klyndyuk.orderservice.repository.ItemRepository;
import com.klyndyuk.orderservice.repository.OrderRepository;
import com.klyndyuk.orderservice.specification.OrderSpecification;
import com.klyndyuk.orderservice.util.TestConstants;
import com.klyndyuk.orderservice.util.TestItems;
import com.klyndyuk.orderservice.util.TestOrders;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByUserIdAndDeletedFalse_shouldReturnOnlyNotDeletedOrders() {
        Item item = itemRepository.save(TestItems.createItemForPersist());

        Order activeOrder = TestOrders.createOrderForPersist();
        TestOrders.withSingleItem(activeOrder, item, 1);
        orderRepository.save(activeOrder);

        Order deletedOrder = TestOrders.createSecondOrderForPersist();
        deletedOrder.setUserId(TestConstants.USER_ID);
        deletedOrder.setDeleted(true);
        TestOrders.withSingleItem(deletedOrder, item, 1);
        orderRepository.save(deletedOrder);

        List<Order> result = orderRepository.findAllByUserIdAndDeletedFalse(TestConstants.USER_ID);

        assertThat(result)
                .singleElement()
                .satisfies(order -> assertThat(order.getId()).isEqualTo(activeOrder.getId()));
    }

    @Test
    void findAllByDeletedFalse_shouldFilterByStatusAndDateRange() {
        Item item = itemRepository.save(TestItems.createItemForPersist());

        Order createdOrder = TestOrders.createOrderForPersist();
        createdOrder.setCreatedAt(Instant.parse("2026-01-10T10:00:00Z"));
        TestOrders.withSingleItem(createdOrder, item, 2);
        createdOrder = orderRepository.save(createdOrder);

        entityManager.createQuery("""
        update Order o
        set o.createdAt = :createdAt
        where o.id = :id
        """)
                .setParameter("createdAt", Instant.parse("2026-01-10T10:00:00Z"))
                .setParameter("id", createdOrder.getId())
                .executeUpdate();

        entityManager.clear();

        Order shippedOrder = TestOrders.createSecondOrderForPersist();
        shippedOrder.setStatus("SHIPPED");
        shippedOrder.setCreatedAt(Instant.parse("2026-01-10T11:00:00Z"));
        shippedOrder.setTotalPrice(new BigDecimal("10.00"));
        TestOrders.withSingleItem(shippedOrder, item, 1);
        orderRepository.save(shippedOrder);

        Page<Order> page = orderRepository.findAllByDeletedFalse(
                OrderSpecification.byFilters(
                        Instant.parse("2026-01-10T00:00:00Z"),
                        Instant.parse("2026-01-10T23:59:59Z"),
                        "CREATED"
                ),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .singleElement()
                .satisfies(order -> {
                    assertThat(order.getStatus()).isEqualTo("CREATED");
                    assertThat(order.getUserId()).isEqualTo(TestConstants.USER_ID);
                });
    }

    @Test
    void findAllByDeletedFalse_shouldReturnPagedResult() {
        Item item = itemRepository.save(TestItems.createItemForPersist());

        Order first = TestOrders.createOrderForPersist();
        TestOrders.withSingleItem(first, item, 1);
        orderRepository.save(first);

        Order second = TestOrders.createSecondOrderForPersist();
        second.setUserId(TestConstants.USER_ID);
        second.setDeleted(false);
        TestOrders.withSingleItem(second, item, 1);
        orderRepository.save(second);

        Page<Order> page = orderRepository.findAllByDeletedFalse(
                OrderSpecification.byFilters(null, null, null),
                PageRequest.of(0, 1)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}
