package com.wanderai.repository;

import com.wanderai.model.Merchant;
import com.wanderai.model.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<TourPackage, Long> {
    List<TourPackage> findByMerchant(Merchant merchant);
    
    Optional<TourPackage> findByIdAndMerchant(Long id, Merchant merchant);
    
    int countByMerchant(Merchant merchant);
    
    int countByMerchantAndStatus(Merchant merchant, String status);
    
    long countByStatus(String status);
    
    @Query("SELECT SUM(p.price * p.soldCount) FROM TourPackage p")
    long calculateTotalSales();
    
    @Query("SELECT SUM(p.price * p.soldCount) FROM TourPackage p WHERE p.createdAt >= ?1")
    long calculateSalesAfter(LocalDateTime date);
}