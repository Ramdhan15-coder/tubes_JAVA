package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;  
import org.springframework.data.repository.query.Param; 
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductIdOrderByReviewDateDesc(Integer productId);

    List<Review> findAllByOrderByReviewDateDesc(); 

    // METODE UNTUK SEARCH
    @Query("SELECT r FROM Review r JOIN r.product p JOIN r.user u WHERE p.name LIKE %:keyword% OR u.username LIKE %:keyword% ORDER BY r.reviewDate DESC")
    List<Review> searchByProductOrUser(@Param("keyword") String keyword);
}