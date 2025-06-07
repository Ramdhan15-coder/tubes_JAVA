package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductIdOrderByReviewDateDesc(Integer productId); // Sudah ada

    // (Opsional) boolean existsByProductIdAndUserId(Integer productId, Integer userId);

    List<Review> findAllByOrderByReviewDateDesc(); // <-- TAMBAHKAN METODE INI
}