package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.CartItem; // Pastikan import model CartItem
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> { // CartItem adalah entitas, Integer adalah tipe ID

    // Metode custom untuk mencari CartItem berdasarkan ID keranjang (shoppingCart.id) DAN ID produk (product.id)
    // Ini berguna untuk mengecek apakah suatu produk sudah ada di dalam keranjang tertentu.
    // Karena kita punya unique constraint pada (cart_id, product_id), ini akan mengembalikan paling banyak satu hasil.
    Optional<CartItem> findByShoppingCartIdAndProductId(Integer shoppingCartId, Integer productId);

    // Metode custom untuk mengambil semua CartItem milik satu ShoppingCart tertentu
    List<CartItem> findByShoppingCartId(Integer shoppingCartId);

    // (Opsional) Metode untuk menghapus CartItem berdasarkan ID keranjang dan ID produk
    // void deleteByShoppingCartIdAndProductId(Integer shoppingCartId, Integer productId);
}