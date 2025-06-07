package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.ShoppingCart; // Pastikan import model ShoppingCart
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> { // ShoppingCart adalah entitas, Integer adalah tipe ID

    // Metode custom untuk mencari ShoppingCart berdasarkan ID user
    // Karena user_id di tabel shopping_carts itu UNIQUE, ini akan mengembalikan paling banyak satu hasil.
    Optional<ShoppingCart> findByUserId(Integer userId);

    // Metode custom untuk mengecek apakah ShoppingCart sudah ada untuk user tertentu
    boolean existsByUserId(Integer userId);
}