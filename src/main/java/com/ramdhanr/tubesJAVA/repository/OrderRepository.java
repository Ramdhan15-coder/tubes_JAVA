package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Order;
import com.ramdhanr.tubesJAVA.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p WHERE o.id = :orderId AND o.user = :user")
    Optional<Order> findByIdAndUserWithItemsAndProducts(@Param("orderId") Integer orderId, @Param("user") User user);

    // PASTIKAN METODE INI SUDAH BENAR
    @Query("SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.orderDate ASC")
    List<Order> findAllWithUserOrderByOrderDateAsc();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p WHERE o.id = :orderId")
    Optional<Order> findByIdWithItemsAndProductsAdmin(@Param("orderId") Integer orderId);
}