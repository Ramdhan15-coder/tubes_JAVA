package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.ShoppingCart; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> { 

    
    Optional<ShoppingCart> findByUserId(Integer userId);
    // Metode custom untuk mengecek apakah ShoppingCart sudah ada untuk user tertentu
    boolean existsByUserId(Integer userId);
}