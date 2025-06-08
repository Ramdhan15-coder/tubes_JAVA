package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.CheckoutFormDto;
import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.Order;
import com.ramdhanr.tubesJAVA.model.User;

import java.util.List;
import java.util.Optional;

public interface OrderService { // Pastikan tidak ada 'extends UserDetailsService' di sini

    /**
     * Membuat pesanan baru berdasarkan item di keranjang dan detail checkout.
     * @param user User yang melakukan pesanan.
     * @param cartItems Daftar item dari keranjang belanja.
     * @param checkoutFormDto Data dari form checkout (alamat pengiriman, catatan).
     * @return Order yang berhasil dibuat.
     */
    Order placeOrder(User user, List<CartItem> cartItems, CheckoutFormDto checkoutFormDto);

    List<Order> getOrdersForUser(User user);

    Optional<Order> findOrderByIdForUser(Integer orderId, User user);

    Order savePaymentProof(Integer orderId, String paymentProofUrl, User currentUser);
    
    // PERBAIKAN DI SINI: Samakan nama metode dengan implementasi dan pemanggilan di controller
    List<Order> getAllOrdersAdmin();

    List<Order> searchOrdersAdmin(String keyword, String status);

    Optional<Order> findOrderByIdAdmin(Integer orderId); // <-- TAMBAHKAN INI

    Order updateOrderStatus(Integer orderId, String newStatus); // <-- TAMBAHKAN INI

    void deleteOrderByIdAdmin(Integer orderId);
}