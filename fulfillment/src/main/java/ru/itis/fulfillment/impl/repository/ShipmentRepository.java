package ru.itis.fulfillment.impl.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.fulfillment.impl.model.ShipmentEntity;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity, Long> {

    @Query("SELECT s FROM ShipmentEntity s JOIN FETCH s.fulfillment WHERE s.id = :id")
    Optional<ShipmentEntity> findByIdWithFulfillment(long id);

    @Query(value = """
            SELECT s FROM ShipmentEntity s JOIN FETCH s.fulfillment f
            WHERE f.account.id = :accountId
               AND (LOWER(f.title) LIKE LOWER(:searchTerm) OR
                    LOWER(f.article) LIKE LOWER(:searchTerm) OR
                    LOWER(f.barcode) LIKE LOWER(:searchTerm))""")
    Slice<ShipmentEntity> findByAccountAndSearchTerm(long accountId, String searchTerm, Pageable pageable);

    @Query(value = """
            SELECT s FROM ShipmentEntity s JOIN FETCH s.fulfillment f
            WHERE f.account.id = :accountId
               AND LOWER(s.status) = LOWER(:status)
               AND (LOWER(f.title) LIKE LOWER(:searchTerm) OR
                    LOWER(f.article) LIKE LOWER(:searchTerm) OR
                    LOWER(f.barcode) LIKE LOWER(:searchTerm))""")
    Slice<ShipmentEntity> findByAccountAndSearchTermAndStatus(long accountId, String searchTerm,
                                                              String status, Pageable pageable);

    @Query("SELECT s FROM ShipmentEntity s JOIN FETCH s.fulfillment f WHERE f.account.id = :accountId")
    Slice<ShipmentEntity> findByAccountId(long accountId, Pageable pageable);

    @Query("""
            SELECT s FROM ShipmentEntity s JOIN FETCH s.fulfillment f
            WHERE f.account.id = :accountId AND LOWER(s.status) = LOWER(:status)""")
    Slice<ShipmentEntity> findByAccountIdAndStatus(long accountId, String status, Pageable pageable);
}
