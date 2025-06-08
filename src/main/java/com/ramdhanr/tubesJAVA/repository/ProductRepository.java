package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

   
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByCategoryNameIgnoreCase(String categoryName);

    // METODE UNTUK SEARCH
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchByNameOrDescription(@Param("keyword") String keyword);
}