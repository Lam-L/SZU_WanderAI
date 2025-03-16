package com.wanderai.service.admin;

import com.wanderai.dto.admin.*;
import com.wanderai.exception.ResourceNotFoundException;
import com.wanderai.model.*;
import com.wanderai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MerchantRepository merchantRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // Merchants
    public List<MerchantAdminDto> getMerchants() {
        return merchantRepository.findAll().stream()
                .map(this::mapMerchantToAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MerchantAdminDto approveMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setStatus("approved");
        return mapMerchantToAdminDto(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantAdminDto rejectMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setStatus("rejected");
        return mapMerchantToAdminDto(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantAdminDto blockMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setStatus("blocked");
        return mapMerchantToAdminDto(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantAdminDto unblockMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setStatus("approved");
        return mapMerchantToAdminDto(merchantRepository.save(merchant));
    }

    @Transactional
    public void deleteMerchant(Long id) {
        merchantRepository.deleteById(id);
    }

    // Tour Packages
    public List<PackageAdminDto> getPackages() {
        return packageRepository.findAll().stream()
                .map(this::mapPackageToAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PackageAdminDto approvePackage(Long id) {
        TourPackage pkg = getPackageById(id);
        pkg.setStatus("active");
        return mapPackageToAdminDto(packageRepository.save(pkg));
    }

    @Transactional
    public PackageAdminDto rejectPackage(Long id) {
        TourPackage pkg = getPackageById(id);
        pkg.setStatus("rejected");
        return mapPackageToAdminDto(packageRepository.save(pkg));
    }

    @Transactional
    public void deletePackage(Long id) {
        packageRepository.deleteById(id);
    }

    // Users
    public List<UserAdminDto> getUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserAdminDto blockUser(Long id) {
        User user = getUserById(id);
        user.setBlocked(true);
        return mapUserToAdminDto(userRepository.save(user));
    }

    @Transactional
    public UserAdminDto unblockUser(Long id) {
        User user = getUserById(id);
        user.setBlocked(false);
        return mapUserToAdminDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Posts
    public List<PostAdminDto> getPosts() {
        return postRepository.findAll().stream()
                .map(this::mapPostToAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostAdminDto hidePost(Long id) {
        Post post = getPostById(id);
        post.setHidden(true);
        return mapPostToAdminDto(postRepository.save(post));
    }

    @Transactional
    public PostAdminDto showPost(Long id) {
        Post post = getPostById(id);
        post.setHidden(false);
        return mapPostToAdminDto(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // Stats
    public AdminStatsDto getStats() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        return AdminStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalPosts(postRepository.count())
                .totalComments(commentRepository.count())
                .newUsersToday(userRepository.countByCreatedAtAfter(today))
                .newPostsToday(postRepository.countByCreatedAtAfter(today))
                .totalMerchants(merchantRepository.count())
                .pendingMerchants(merchantRepository.countByStatus("pending"))
                .totalPackages(packageRepository.count())
                .pendingPackages(packageRepository.countByStatus("pending"))
                .totalSales(packageRepository.calculateTotalSales())
                .salesToday(packageRepository.calculateSalesAfter(today))
                .build();
    }

    // Helper methods
    private Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));
    }

    private TourPackage getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
    }

    // Mapping methods
    private MerchantAdminDto mapMerchantToAdminDto(Merchant merchant) {
        return MerchantAdminDto.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .phone(merchant.getPhone())
                .address(merchant.getAddress())
                .businessLicense(merchant.getBusinessLicense())
                .status(merchant.getStatus())
                .createdAt(merchant.getCreatedAt())
                .packageCount(merchant.getPackages().size())
                .totalSales(calculateMerchantTotalSales(merchant))
                .build();
    }

    private PackageAdminDto mapPackageToAdminDto(TourPackage pkg) {
        return PackageAdminDto.builder()
                .id(pkg.getId())
                .title(pkg.getTitle())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .originalPrice(pkg.getOriginalPrice())
                .location(pkg.getLocation())
                .duration(pkg.getDuration())
                .merchant(new PackageAdminDto.MerchantSimpleDto(
                    pkg.getMerchant().getId(),
                    pkg.getMerchant().getName()
                ))
                .status(pkg.getStatus())
                .soldCount(pkg.getSoldCount())
                .createdAt(pkg.getCreatedAt())
                .features(pkg.getFeatures())
                .itinerary(pkg.getItinerary().stream()
                    .map(i -> new PackageAdminDto.ItineraryDto(
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

    private UserAdminDto mapUserToAdminDto(User user) {
        return UserAdminDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .blocked(user.isBlocked())
                .postCount(user.getPosts().size())
                .commentCount(user.getComments().size())
                .build();
    }

    private PostAdminDto mapPostToAdminDto(Post post) {
        return PostAdminDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(post.getImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList()))
                .author(new PostAdminDto.UserSimpleDto(
                    post.getAuthor().getId(),
                    post.getAuthor().getUserName()
                ))
                .createdAt(post.getCreatedAt())
                .likes(post.getLikes().size())
                .comments(post.getComments().size())
                .hidden(post.isHidden())
                .build();
    }

    private double calculateMerchantTotalSales(Merchant merchant) {
        return merchant.getPackages().stream()
                .mapToDouble(pkg -> pkg.getPrice() * pkg.getSoldCount())
                .sum();
    }
}
