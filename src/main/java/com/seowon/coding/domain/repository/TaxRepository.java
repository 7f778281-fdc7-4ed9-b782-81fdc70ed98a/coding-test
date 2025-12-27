package com.seowon.coding.domain.repository;

import com.seowon.coding.domain.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    @Query(value = "SELECT t from Tax t where t.productId = :productId and t.location = :location")
    Tax getTaxByProduct(@Param("productId") Long productId, @Param("location") String location);
}
