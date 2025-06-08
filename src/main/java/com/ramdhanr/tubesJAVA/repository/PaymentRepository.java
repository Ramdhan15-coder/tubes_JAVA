package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByOrderId(Integer orderId);

    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    List<Payment> findByPaymentMethodIgnoreCase(String paymentMethod);
}