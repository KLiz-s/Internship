package com.klyndyuk.orderservice.mapper;

import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})

public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    Order toEntity(CreateOrderRequest createOrderRequest);

    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "items", ignore = true)
    void updateEntityFromRequest(UpdateOrderRequest updateOrderRequest, @MappingTarget Order order);
}
