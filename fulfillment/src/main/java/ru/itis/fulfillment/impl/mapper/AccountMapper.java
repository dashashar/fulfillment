package ru.itis.fulfillment.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.itis.fulfillment.api.dto.internal.request.RegistrationRequest;
import ru.itis.fulfillment.api.dto.internal.response.AccountResponse;
import ru.itis.fulfillment.api.dto.internal.response.ChangeKeyResponse;
import ru.itis.fulfillment.impl.model.AccountEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    AccountEntity toEntity(RegistrationRequest request);

    AccountResponse toResponse(AccountEntity entity);

    @Mapping(target = "message", constant = "Wb api key has been successfully updated")
    @Mapping(target = "newKeyLastChars", expression = "java(\"***\" + newKeyLastChars)")
    ChangeKeyResponse toResponse(String newKeyLastChars);
}
