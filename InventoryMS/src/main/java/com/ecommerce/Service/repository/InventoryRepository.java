package com.ecommerce.Service.repository;

import com.ecommerce.Service.entity.Inventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.availableQuantity = i.availableQuantity - :quantity,
            i.reservedQuantity = i.reservedQuantity + :quantity
        WHERE i.productId = :productId
        AND i.availableQuantity >= :quantity
    """)
    int reserveStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);


    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.availableQuantity = i.availableQuantity + :quantity,
            i.reservedQuantity = i.reservedQuantity - :quantity
        WHERE i.productId = :productId
        AND i.reservedQuantity >= :quantity
    """)
    int releaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}