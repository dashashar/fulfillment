package ru.itis.fulfillment.impl.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.fulfillment.impl.model.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByAccount_Id(long accountId);

    void deleteAllByAccount_Id(long accountId);

    void deleteByAccount_IdAndBarcodeIn(Long accountId, List<String> barcodes);

    @Query(value = """
            SELECT * FROM product p WHERE p.account_id = :accountId
            AND (LOWER(p.title) LIKE :searchTerm OR
            LOWER(p.article) LIKE :searchTerm OR
            LOWER(p.barcode) LIKE :searchTerm)""",
            nativeQuery = true)
    Slice<ProductEntity> findByAccountAndSearchTerm(long accountId, String searchTerm, Pageable pageable);

    Slice<ProductEntity> findByAccount_Id(long accountId, Pageable pageable);
}
