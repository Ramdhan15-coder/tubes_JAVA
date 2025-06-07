package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.AdminProductCreateDto;
import com.ramdhanr.tubesJAVA.dto.AdminProductUpdateDto; // <-- IMPORT DTO UPDATE
import com.ramdhanr.tubesJAVA.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> findProductById(Integer productId); // Sudah ada

    Product createProduct(AdminProductCreateDto productCreateDto); // Sudah ada

    Product updateProduct(Integer productId, AdminProductUpdateDto productUpdateDto); // <-- TAMBAHKAN METODE INI

    void decreaseStock(Integer productId, int quantity);
    
    void deleteProductById(Integer productId); 
}