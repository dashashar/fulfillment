package ru.itis.fulfillment.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.fulfillment.impl.model.AccountEntity;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByTelegramId(long telegramId);

    boolean existsByTelegramId(Long telegramId);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    boolean existsByWbApiKey(String wbApiKey);

    @Query("SELECT a.wbApiKey FROM AccountEntity a WHERE a.id = :id")
    Optional<String> getWbApiKeyById(long id);

}
