package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findByCategoryNameIgnoreCase(String categoryName);
}