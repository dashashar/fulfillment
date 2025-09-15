package ru.itis.fulfillment.impl.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.fulfillment.impl.model.FulfillmentEntity;

import java.util.Optional;

public interface FulfillmentRepository extends JpaRepository<FulfillmentEntity, Long> {

    @Query(value = """
            SELECT * FROM fulfillment f WHERE f.account_id = :accountId
            AND (LOWER(f.title) LIKE :searchTerm OR
            LOWER(f.article) LIKE :searchTerm OR
            LOWER(f.barcode) LIKE :searchTerm)""",
            nativeQuery = true)
    Slice<FulfillmentEntity> findByAccountAndSearchTerm(long accountId, String searchTerm, Pageable pageable);

    Slice<FulfillmentEntity> findByAccount_Id(long accountId, Pageable pageable);

    @Query("SELECT a.name FROM FulfillmentEntity f JOIN f.account a WHERE f.id = :fulfillmentId")
    Optional<String> findAccountNameByFulfillmentId(long fulfillmentId);
}
