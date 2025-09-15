package ru.itis.fulfillment.impl.service;

import ru.itis.fulfillment.api.dto.internal.request.ChangeKeyRequest;
import ru.itis.fulfillment.api.dto.internal.request.RegistrationRequest;
import ru.itis.fulfillment.api.dto.internal.response.ChangeKeyResponse;
import ru.itis.fulfillment.api.dto.internal.response.AccountResponse;
import ru.itis.fulfillment.api.dto.internal.response.KeyResponse;

public interface AccountService {

    AccountResponse register(RegistrationRequest request, long telegramId);

    KeyResponse getWbApiKey(long id);

    ChangeKeyResponse changeWbApiKey(ChangeKeyRequest request, long id);

    AccountResponse getAccount(long accountId);

    void deleteAccount(long accountId);
}
