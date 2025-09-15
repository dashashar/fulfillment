package ru.itis.fulfillment.impl.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.fulfillment.api.api.AccountApi;
import ru.itis.fulfillment.api.dto.internal.request.ChangeKeyRequest;
import ru.itis.fulfillment.api.dto.internal.request.RegistrationRequest;
import ru.itis.fulfillment.api.dto.internal.response.AccountResponse;
import ru.itis.fulfillment.api.dto.internal.response.ChangeKeyResponse;
import ru.itis.fulfillment.api.dto.internal.response.KeyResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;
import ru.itis.fulfillment.impl.security.validator.TelegramAuthValidator;
import ru.itis.fulfillment.impl.service.AccountService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController implements AccountApi {

    private final AccountService accountService;
    private final TelegramAuthValidator authValidator;

    @Override
    public AccountResponse register(String initData, RegistrationRequest request) {
        long telegramId = authValidator.validate(initData);
        AccountResponse registration = accountService.register(request, telegramId);
        log.info("A new account has registered: {}", registration);
        return registration;
    }

    @Override
    public KeyResponse getWbApiKey(AccountPrincipal account) {
        return accountService.getWbApiKey(account.getId());
    }

    @Override
    public ChangeKeyResponse changeWbApiKey(AccountPrincipal account, ChangeKeyRequest request) {
        ChangeKeyResponse response = accountService.changeWbApiKey(request, account.getId());
        log.debug("The account with id: {} has changed the wb api key: {}", account.getId(), response.newKeyLastChars());
        return response;
    }

    @Override
    public AccountResponse getAccount(AccountPrincipal account) {
        return accountService.getAccount(account.getId());
    }

    @Override
    public void deleteAccount(AccountPrincipal account) {
        accountService.deleteAccount(account.getId());
    }
}
