package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.util.List; // Tidak perlu custom method dulu untuk awal

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // List<OrderItem> findByOrderId(Integer orderId); // Bisa ditambahkan jika sering butuh
}