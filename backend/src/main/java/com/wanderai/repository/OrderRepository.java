package com.wanderai.repository;

import com.wanderai.model.Merchant;
import com.wanderai.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMerchant(Merchant merchant);
    
    Optional<Order> findByIdAndMerchant(Long id, Merchant merchant);
    
    int countByMerchant(Merchant merchant);
    
    int countByMerchantAndStatus(Merchant merchant, String status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.merchant = ?1")
    Double calculateTotalSalesByMerchant(Merchant merchant);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.merchant = ?1 AND o.createdAt >= ?2")
    Double calculateSalesByMerchantAfter(Merchant merchant, LocalDateTime date);
}