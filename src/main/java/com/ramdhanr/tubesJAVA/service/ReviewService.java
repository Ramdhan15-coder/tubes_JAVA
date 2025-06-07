package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.AdminReviewUpdateDto; // <-- IMPORT DTO UPDATE REVIEW
import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.Review;
import com.ramdhanr.tubesJAVA.model.User;

import java.util.List;
import java.util.Optional; // <-- IMPORT OPTIONAL

public interface ReviewService {

    List<Review> getReviewsByProductId(Integer productId);

    Review saveReview(ReviewDto reviewDto, Integer productId, User currentUser);

    List<Review> getAllReviews();

    void deleteReviewById(Integer reviewId); // Sudah ada

    Optional<Review> findReviewById(Integer reviewId); // <-- TAMBAHKAN METODE INI

    Review updateReviewByAdmin(Integer reviewId, AdminReviewUpdateDto reviewUpdateDto); // <-- TAMBAHKAN METODE INI
}