package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.ShoppingCart;
import com.ramdhanr.tubesJAVA.model.User; // User yang memiliki keranjang

import java.math.BigDecimal;
import java.util.List; // Untuk menampilkan isi keranjang

public interface CartService {

    /**
     * Mendapatkan keranjang belanja untuk user tertentu.
     * Jika belum ada, akan membuat keranjang baru untuk user tersebut.
     * @param user Pengguna yang memiliki keranjang.
     * @return ShoppingCart milik pengguna.
     */
    ShoppingCart getCartForUser(User user);

    /**
     * Menambahkan produk ke keranjang belanja pengguna.
     * Jika produk sudah ada di keranjang, akan menambah quantity-nya.
     * @param user Pengguna yang memiliki keranjang.
     * @param productId ID produk yang akan ditambahkan.
     * @param quantity Jumlah produk yang akan ditambahkan.
     * @return CartItem yang baru ditambahkan atau diupdate.
     */
    CartItem addProductToCart(User user, Integer productId, int quantity);

    /**
     * Mengupdate quantity item tertentu di dalam keranjang.
     * Jika quantity menjadi 0 atau kurang, item akan dihapus.
     * @param user Pengguna yang memiliki keranjang.
     * @param cartItemId ID dari CartItem yang akan diupdate.
     * @param quantity Quantity baru.
     * @return CartItem yang sudah diupdate, atau null jika item dihapus.
     */
    CartItem updateCartItemQuantity(User user, Integer cartItemId, int quantity);

    /**
     * Menghapus satu item dari keranjang belanja pengguna.
     * @param user Pengguna yang memiliki keranjang.
     * @param cartItemId ID dari CartItem yang akan dihapus.
     */
    void removeCartItem(User user, Integer cartItemId);

    /**
     * Mengosongkan semua item dari keranjang belanja pengguna.
     * @param user Pengguna yang memiliki keranjang.
     */
    void clearCart(User user);

    /**
     * Menghitung total harga dari semua item di keranjang belanja pengguna.
     * @param user Pengguna yang memiliki keranjang.
     * @return BigDecimal yang merepresentasikan total harga.
     */
    BigDecimal getTotalCartPrice(User user);

    /**
     * Mendapatkan semua item dalam keranjang pengguna.
     * @param user Pengguna yang memiliki keranjang.
     * @return List dari CartItem.
     */
    List<CartItem> getCartItems(User user);
}