package com.klyndyuk.userservice.mapper;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserResponse;
import com.klyndyuk.userservice.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
