package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.dto.AdminReviewUpdateDto; 
import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.model.Review;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.repository.ReviewRepository;
import com.ramdhanr.tubesJAVA.service.ProductService;
import com.ramdhanr.tubesJAVA.service.ReviewService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; 

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService; 

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductId(Integer productId) {
        return reviewRepository.findByProductIdOrderByReviewDateDesc(productId);
    }

    @Override
    @Transactional
    public Review saveReview(ReviewDto reviewDto, Integer productId, User currentUser) {
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new RuntimeException("Produk dengan ID " + productId + " tidak ditemukan."));
        Review newReview = new Review();
        newReview.setProduct(product);
        newReview.setUser(currentUser);
        newReview.setRating(reviewDto.rating());
        newReview.setComment(reviewDto.comment());
        return reviewRepository.save(newReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByReviewDateDesc();
    }

    @Override
    @Transactional
    public void deleteReviewById(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Error: Review dengan ID " + reviewId + " tidak ditemukan. Tidak bisa dihapus.");
        }
        try {
            reviewRepository.deleteById(reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Error: Gagal menghapus review dengan ID " + reviewId + ". Review mungkin sudah dihapus.", e);
        }
    }

    // IMPLEMENTASI METODE UNTUK MENCARI REVIEW BERDASARKAN ID
    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    // IMPLEMENTASI METODE UNTUK UPDATE REVIEW OLEH ADMIN
    @Override
    @Transactional // Operasi ini mengubah data
    public Review updateReviewByAdmin(Integer reviewId, AdminReviewUpdateDto reviewUpdateDto) {
        // 1. Ambil review yang akan diupdate dari database
        Review reviewToUpdate = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Error: Review dengan ID " + reviewId + " tidak ditemukan. Update gagal."));

        // 2. Update field rating dan comment dari DTO
        reviewToUpdate.setRating(reviewUpdateDto.rating());
        reviewToUpdate.setComment(reviewUpdateDto.comment());
        // reviewDate, user, dan product tidak diubah oleh admin pada proses edit ini
        // updatedAt akan otomatis diupdate oleh @UpdateTimestamp

        // 3. Simpan perubahan ke database
        return reviewRepository.save(reviewToUpdate);
    }

    @Override
@Transactional(readOnly = true)
public List<Review> searchReviews(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return reviewRepository.findAllByOrderByReviewDateDesc(); // Jika keyword kosong, kembalikan semua review
    }
    return reviewRepository.searchByProductOrUser(keyword);
}
}