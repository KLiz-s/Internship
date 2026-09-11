package com.klyndyuk.orderservice.mapper;

import com.klyndyuk.orderservice.dto.request.CreateOrderItemRequest;
import com.klyndyuk.orderservice.dto.response.OrderItemResponse;
import com.klyndyuk.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItem toEntity(CreateOrderItemRequest createOrderItemRequest);

    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    OrderItemResponse toResponse(OrderItem orderItem);
}
