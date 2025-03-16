package com.wanderai.repository;

import com.wanderai.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    long countByStatus(String status);
    
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.createdAt >= ?1")
    long countByCreatedAtAfter(LocalDateTime date);
}