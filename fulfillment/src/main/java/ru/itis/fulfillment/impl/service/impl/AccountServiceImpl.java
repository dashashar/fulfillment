package ru.itis.fulfillment.impl.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.fulfillment.api.dto.internal.request.ChangeKeyRequest;
import ru.itis.fulfillment.api.dto.internal.request.RegistrationRequest;
import ru.itis.fulfillment.api.dto.internal.response.AccountResponse;
import ru.itis.fulfillment.api.dto.internal.response.ChangeKeyResponse;
import ru.itis.fulfillment.api.dto.internal.response.KeyResponse;
import ru.itis.fulfillment.impl.exception.ConflictException;
import ru.itis.fulfillment.impl.exception.DatabaseException;
import ru.itis.fulfillment.impl.exception.NotFoundException;
import ru.itis.fulfillment.impl.mapper.AccountMapper;
import ru.itis.fulfillment.impl.model.AccountEntity;
import ru.itis.fulfillment.impl.repository.AccountRepository;
import ru.itis.fulfillment.impl.service.AccountService;
import ru.itis.fulfillment.impl.service.ProductService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse register(RegistrationRequest request, long telegramId) {
        try {
            if (accountRepository.existsByTelegramId(telegramId)) {
                log.warn("Account with this telegram id: {} already exists", telegramId);
                throw new ConflictException("Account with this telegram id already exists");
            }
            if (accountRepository.existsByWbApiKey(request.wbApiKey())) {
                log.warn("Account with this wb api key already exists");
                throw new ConflictException("Account with this wb api key already exists");
            }
            if (accountRepository.existsByEmail(request.email())) {
                log.warn("Account with this email: {} already exists", request.email());
                throw new ConflictException("Account with this email already exists");
            }
            if (accountRepository.existsByName(request.name())) {
                log.warn("Account with this name: {} already exists", request.phone());
                throw new ConflictException("Account with this name already exists");
            }
            AccountEntity account = accountMapper.toEntity(request);
            account.setTelegramId(telegramId);
            AccountEntity savedAccount = accountRepository.save(account);
            productService.savingProductsAfterRegistration(savedAccount.getWbApiKey(), savedAccount.getId());
            return accountMapper.toResponse(savedAccount);
        } catch (DataAccessException e) {
            log.error("Couldn't save account: {}, {}", request, e.getMessage(), e);
            throw new DatabaseException("Couldn't save account", e);
        }
    }

    @Override
    public KeyResponse getWbApiKey(long id) {
        try {
            String apiKey = accountRepository.getWbApiKeyById(id)
                    .orElseThrow(() -> new NotFoundException("Wb api key was not found for this account"));
            return new KeyResponse(apiKey);
        } catch (DataAccessException e) {
            log.error("Couldn't get wb api key for the account with id: {}, {}", id, e.getMessage(), e);
            throw new DatabaseException("Couldn't get wb api key", e);
        }
    }

    @Override
    @Transactional
    public ChangeKeyResponse changeWbApiKey(ChangeKeyRequest request, long accountId) {
        try {
            AccountEntity account = accountRepository.findById(accountId).orElseThrow(
                    () -> new NotFoundException("Account not found"));
            if (accountRepository.existsByWbApiKey(request.wbApiKey())) {
                log.warn("An attempt to save a key that already exists");
                throw new ConflictException("It is not possible to save this wb api key, it already exists in the database");
            }
            account.setWbApiKey(request.wbApiKey());
            String updatedKey = accountRepository.save(account).getWbApiKey();
            return accountMapper.toResponse(updatedKey.length() >= 6 ?
                    updatedKey.substring(updatedKey.length() - 6) : updatedKey);
        } catch (DataAccessException e) {
            log.error("Couldn't change the wb api key for the user with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't change the wb api key", e);
        }
    }

    @Override
    public AccountResponse getAccount(long accountId) {
        try {
            AccountEntity account = accountRepository.findById(accountId).orElseThrow(
                    () -> new NotFoundException("Account not found"));
            return accountMapper.toResponse(account);
        } catch (DataAccessException e) {
            log.error("Couldn't get account data with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't get account data", e);
        }
    }

    @Override
    public void deleteAccount(long accountId) {
        try {
            accountRepository.deleteById(accountId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Attempt to delete a non-existent account with id: {}, {}", accountId, e.getMessage());
            throw new NotFoundException("Account to delete was not found");
        } catch (DataAccessException e) {
            log.error("Couldn't delete account with id: {}, {}", accountId, e.getMessage(), e);
            throw new DatabaseException("Couldn't delete account", e);
        }
    }
}
