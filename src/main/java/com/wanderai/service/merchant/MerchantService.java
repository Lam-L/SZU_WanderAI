package com.wanderai.service.merchant;

import com.wanderai.dto.merchant.*;
import com.wanderai.exception.BadRequestException;
import com.wanderai.exception.ResourceNotFoundException;
import com.wanderai.model.*;
import com.wanderai.repository.*;
import com.wanderai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final PackageRepository packageRepository;
    private final OrderRepository orderRepository;

    private Merchant getCurrentMerchant() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return merchantRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", userPrincipal.getId()));
    }

    // Profile
    public MerchantProfileDto getProfile() {
        return mapMerchantToProfileDto(getCurrentMerchant());
    }

    @Transactional
    public MerchantProfileDto updateProfile(MerchantUpdateDto updateDto) {
        Merchant merchant = getCurrentMerchant();
        merchant.setName(updateDto.getName());
        merchant.setPhone(updateDto.getPhone());
        merchant.setAddress(updateDto.getAddress());
        return mapMerchantToProfileDto(merchantRepository.save(merchant));
    }

    // Packages
    public List<PackageDto> getPackages() {
        Merchant merchant = getCurrentMerchant();
        return packageRepository.findByMerchant(merchant).stream()
                .map(this::mapPackageToDto)
                .collect(Collectors.toList());
    }

    public PackageDetailDto getPackageById(Long id) {
        Merchant merchant = getCurrentMerchant();
        TourPackage pkg = packageRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
        return mapPackageToDetailDto(pkg);
    }

    @Transactional
    public PackageDetailDto createPackage(PackageCreateDto createDto) {
        Merchant merchant = getCurrentMerchant();
        
        TourPackage pkg = TourPackage.builder()
                .merchant(merchant)
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .price(createDto.getPrice())
                .originalPrice(createDto.getOriginalPrice())
                .location(createDto.getLocation())
                .duration(createDto.getDuration())
                .features(createDto.getFeatures())
                .includes(createDto.getIncludes())
                .excludes(createDto.getExcludes())
                .notes(createDto.getNotes())
                .status("pending")
                .soldCount(0)
                .build();
        
        // Map itinerary
        List<PackageItinerary> itinerary = createDto.getItinerary().stream()
                .map(i -> PackageItinerary.builder()
                        .day(i.getDay())
                        .title(i.getTitle())
                        .description(i.getDescription())
                        .tourPackage(pkg)
                        .build())
                .collect(Collectors.toList());
        pkg.setItinerary(itinerary);
        
        return mapPackageToDetailDto(packageRepository.save(pkg));
    }

    @Transactional
    public PackageDetailDto updatePackage(Long id, PackageUpdateDto updateDto) {
        Merchant merchant = getCurrentMerchant();
        TourPackage pkg = packageRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
        
        if (pkg.getStatus().equals("active")) {
            throw new BadRequestException("Cannot update an active package");
        }
        
        pkg.setTitle(updateDto.getTitle());
        pkg.setDescription(updateDto.getDescription());
        pkg.setPrice(updateDto.getPrice());
        pkg.setOriginalPrice(updateDto.getOriginalPrice());
        pkg.setLocation(updateDto.getLocation());
        pkg.setDuration(updateDto.getDuration());
        pkg.setFeatures(updateDto.getFeatures());
        pkg.setIncludes(updateDto.getIncludes());
        pkg.setExcludes(updateDto.getExcludes());
        pkg.setNotes(updateDto.getNotes());
        
        // Update itinerary
        pkg.getItinerary().clear();
        List<PackageItinerary> itinerary = updateDto.getItinerary().stream()
                .map(i -> PackageItinerary.builder()
                        .day(i.getDay())
                        .title(i.getTitle())
                        .description(i.getDescription())
                        .tourPackage(pkg)
                        .build())
                .collect(Collectors.toList());
        pkg.setItinerary(itinerary);
        
        return mapPackageToDetailDto(packageRepository.save(pkg));
    }

    @Transactional
    public void deletePackage(Long id) {
        Merchant merchant = getCurrentMerchant();
        TourPackage pkg = packageRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
        
        if (pkg.getStatus().equals("active")) {
            throw new BadRequestException("Cannot delete an active package");
        }
        
        packageRepository.delete(pkg);
    }

    // Orders
    public List<OrderDto> getOrders() {
        Merchant merchant = getCurrentMerchant();
        return orderRepository.findByMerchant(merchant).stream()
                .map(this::mapOrderToDto)
                .collect(Collectors.toList());
    }

    public OrderDetailDto getOrderById(Long id) {
        Merchant merchant = getCurrentMerchant();
        Order order = orderRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapOrderToDetailDto(order);
    }

    @Transactional
    public OrderDetailDto confirmOrder(Long id) {
        Merchant merchant = getCurrentMerchant();
        Order order = orderRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        if (!order.getStatus().equals("pending")) {
            throw new BadRequestException("Only pending orders can be confirmed");
        }
        
        order.setStatus("confirmed");
        order.setConfirmedAt(LocalDateTime.now());
        return mapOrderToDetailDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDetailDto cancelOrder(Long id, CancelOrderDto cancelDto) {
        Merchant merchant = getCurrentMerchant();
        Order order = orderRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        if (!order.getStatus().equals("pending")) {
            throw new BadRequestException("Only pending orders can be canceled");
        }
        
        order.setStatus("canceled");
        order.setCancelReason(cancelDto.getReason());
        order.setCanceledAt(LocalDateTime.now());
        return mapOrderToDetailDto(orderRepository.save(order));
    }

    // Stats
    public MerchantStatsDto getStats() {
        Merchant merchant = getCurrentMerchant();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthStart = today.withDayOfMonth(1);
        
        return MerchantStatsDto.builder()
                .totalPackages(packageRepository.countByMerchant(merchant))
                .activePackages(packageRepository.countByMerchantAndStatus(merchant, "active"))
                .pendingPackages(packageRepository.countByMerchantAndStatus(merchant, "pending"))
                .totalOrders(orderRepository.countByMerchant(merchant))
                .pendingOrders(orderRepository.countByMerchantAndStatus(merchant, "pending"))
                .totalSales(orderRepository.calculateTotalSalesByMerchant(merchant))
                .todaySales(orderRepository.calculateSalesByMerchantAfter(merchant, today))
                .monthSales(orderRepository.calculateSalesByMerchantAfter(merchant, monthStart))
                .build();
    }

    // Mapping methods
    private MerchantProfileDto mapMerchantToProfileDto(Merchant merchant) {
        return MerchantProfileDto.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .phone(merchant.getPhone())
                .address(merchant.getAddress())
                .businessLicense(merchant.getBusinessLicense())
                .status(merchant.getStatus())
                .createdAt(merchant.getCreatedAt())
                .lastLogin(merchant.getLastLogin())
                .packageCount(merchant.getPackages().size())
                .totalSales(calculateMerchantTotalSales(merchant))
                .build();
    }

    private PackageDto mapPackageToDto(TourPackage pkg) {
        return PackageDto.builder()
                .id(pkg.getId())
                .title(pkg.getTitle())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .originalPrice(pkg.getOriginalPrice())
                .location(pkg.getLocation())
                .duration(pkg.getDuration())
                .status(pkg.getStatus())
                .soldCount(pkg.getSoldCount())
                .createdAt(pkg.getCreatedAt())
                .build();
    }

    private PackageDetailDto mapPackageToDetailDto(TourPackage pkg) {
        return PackageDetailDto.builder()
                .id(pkg.getId())
                .title(pkg.getTitle())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .originalPrice(pkg.getOriginalPrice())
                .location(pkg.getLocation())
                .duration(pkg.getDuration())
                .status(pkg.getStatus())
                .soldCount(pkg.getSoldCount())
                .createdAt(pkg.getCreatedAt())
                .features(pkg.getFeatures())
                .itinerary(pkg.getItinerary().stream()
                    .map(i -> new PackageDetailDto.ItineraryDto(
                        i.getDay(),
                        i.getTitle(),
                        i.getDescription()
                    ))
                    .collect(Collectors.toList()))
                .includes(pkg.getIncludes())
                .excludes(pkg.getExcludes())
                .notes(pkg.getNotes())
                .build();
    }

    private OrderDto mapOrderToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tourPackage(new OrderDto.PackageSimpleDto(
                    order.getTourPackage().getId(),
                    order.getTourPackage().getTitle()
                ))
                .user(new OrderDto.UserSimpleDto(
                    order.getUser().getId(),
                    order.getUser().getUserName(),
                    order.getUser().getPhone()
                ))
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailDto mapOrderToDetailDto(Order order) {
        return OrderDetailDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tourPackage(new OrderDetailDto.PackageSimpleDto(
                    order.getTourPackage().getId(),
                    order.getTourPackage().getTitle(),
                    order.getTourPackage().getPrice()
                ))
                .user(new OrderDetailDto.UserSimpleDto(
                    order.getUser().getId(),
                    order.getUser().getUserName(),
                    order.getUser().getEmail(),
                    order.getUser().getPhone()
                ))
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .notes(order.getNotes())
                .cancelReason(order.getCancelReason())
                .confirmedAt(order.getConfirmedAt())
                .canceledAt(order.getCanceledAt())
                .build();
    }

    private double calculateMerchantTotalSales(Merchant merchant) {
        return merchant.getPackages().stream()
                .mapToDouble(pkg -> pkg.getPrice() * pkg.getSoldCount())
                .sum();
    }
}
