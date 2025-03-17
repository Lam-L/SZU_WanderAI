package com.wanderai.controller.admin;

import com.wanderai.dto.admin.*;
import com.wanderai.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final AdminService adminService;

    // Merchants
    @GetMapping("/merchants")
    public ResponseEntity<List<MerchantAdminDto>> getMerchants() {
        return ResponseEntity.ok(adminService.getMerchants());
    }

    @PostMapping("/merchants/{id}/approve")
    public ResponseEntity<MerchantAdminDto> approveMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveMerchant(id));
    }

    @PostMapping("/merchants/{id}/reject")
    public ResponseEntity<MerchantAdminDto> rejectMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectMerchant(id));
    }

    @PostMapping("/merchants/{id}/block")
    public ResponseEntity<MerchantAdminDto> blockMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockMerchant(id));
    }

    @PostMapping("/merchants/{id}/unblock")
    public ResponseEntity<MerchantAdminDto> unblockMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unblockMerchant(id));
    }

    @DeleteMapping("/merchants/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        adminService.deleteMerchant(id);
        return ResponseEntity.ok().build();
    }

    // Tour Packages
    @GetMapping("/packages")
    public ResponseEntity<List<PackageAdminDto>> getPackages() {
        return ResponseEntity.ok(adminService.getPackages());
    }

    @PostMapping("/packages/{id}/approve")
    public ResponseEntity<PackageAdminDto> approvePackage(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approvePackage(id));
    }

    @PostMapping("/packages/{id}/reject")
    public ResponseEntity<PackageAdminDto> rejectPackage(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectPackage(id));
    }

    @DeleteMapping("/packages/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        adminService.deletePackage(id);
        return ResponseEntity.ok().build();
    }

    // Users
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDto>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<UserAdminDto> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }

    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<UserAdminDto> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unblockUser(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Posts
    @GetMapping("/posts")
    public ResponseEntity<List<PostAdminDto>> getPosts() {
        return ResponseEntity.ok(adminService.getPosts());
    }

    @PostMapping("/posts/{id}/hide")
    public ResponseEntity<PostAdminDto> hidePost(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.hidePost(id));
    }

    @PostMapping("/posts/{id}/show")
    public ResponseEntity<PostAdminDto> showPost(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.showPost(id));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // Dashboard Stats
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
