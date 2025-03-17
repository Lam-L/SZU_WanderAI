package com.wanderai.controller.merchant;

import com.wanderai.dto.merchant.*;
import com.wanderai.service.merchant.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@PreAuthorize("hasRole('MERCHANT')")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    // Profile
    @GetMapping("/profile")
    public ResponseEntity<MerchantProfileDto> getProfile() {
        return ResponseEntity.ok(merchantService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<MerchantProfileDto> updateProfile(@Valid @RequestBody MerchantUpdateDto updateDto) {
        return ResponseEntity.ok(merchantService.updateProfile(updateDto));
    }

    // Packages
    @GetMapping("/packages")
    public ResponseEntity<List<PackageDto>> getPackages() {
        return ResponseEntity.ok(merchantService.getPackages());
    }

    @GetMapping("/packages/{id}")
    public ResponseEntity<PackageDetailDto> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getPackageById(id));
    }

    @PostMapping("/packages")
    public ResponseEntity<PackageDetailDto> createPackage(@Valid @RequestBody PackageCreateDto createDto) {
        return ResponseEntity.ok(merchantService.createPackage(createDto));
    }

    @PutMapping("/packages/{id}")
    public ResponseEntity<PackageDetailDto> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody PackageUpdateDto updateDto) {
        return ResponseEntity.ok(merchantService.updatePackage(id, updateDto));
    }

    @DeleteMapping("/packages/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        merchantService.deletePackage(id);
        return ResponseEntity.ok().build();
    }

    // Orders
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders() {
        return ResponseEntity.ok(merchantService.getOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getOrderById(id));
    }

    @PostMapping("/orders/{id}/confirm")
    public ResponseEntity<OrderDetailDto> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.confirmOrder(id));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(
            @PathVariable Long id,
            @RequestBody CancelOrderDto cancelDto) {
        return ResponseEntity.ok(merchantService.cancelOrder(id, cancelDto));
    }

    // Stats
    @GetMapping("/stats")
    public ResponseEntity<MerchantStatsDto> getStats() {
        return ResponseEntity.ok(merchantService.getStats());
    }
}
