package ru.itis.fulfillment.impl.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.fulfillment.impl.exception.AuthenticationServiceException;
import ru.itis.fulfillment.impl.model.AccountEntity;
import ru.itis.fulfillment.impl.repository.AccountRepository;
import ru.itis.fulfillment.impl.security.token.TelegramAuthentication;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;
import ru.itis.fulfillment.impl.security.validator.TelegramAuthValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramAuthenticationService {

    private final AccountRepository accountRepository;
    private final TelegramAuthValidator telegramValidator;

    public TelegramAuthentication authenticate(String initData) {
        long telegramId = telegramValidator.validate(initData);
        AccountEntity account = accountRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new AuthenticationServiceException("Account not found"));
        return new TelegramAuthentication(new AccountPrincipal(account));
    }
}
