package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUserId(Integer userId);

    List<Transaction> findByUserIdOrderByDateDesc(Integer userId);

    List<Transaction> findAllByOrderByDateDesc();
}