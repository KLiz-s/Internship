package com.klyndyuk.authservice.mapper;

import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.entity.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
    Credentials fromRegistrationRequest(RegistrationRequest registrationRequest);
}
