package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.CartItem; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> { // CartItem adalah entitas, Integer adalah tipe ID

   
    Optional<CartItem> findByShoppingCartIdAndProductId(Integer shoppingCartId, Integer productId);

    // Metode custom untuk mengambil semua CartItem milik satu ShoppingCart tertentu
    List<CartItem> findByShoppingCartId(Integer shoppingCartId);

}