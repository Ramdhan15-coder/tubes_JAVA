package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.AdminProductCreateDto;
import com.ramdhanr.tubesJAVA.dto.AdminProductUpdateDto; 
import com.ramdhanr.tubesJAVA.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> findProductById(Integer productId); 

    Product createProduct(AdminProductCreateDto productCreateDto); 

    Product updateProduct(Integer productId, AdminProductUpdateDto productUpdateDto); 

    List<Product> searchProducts(String keyword);

    void decreaseStock(Integer productId, int quantity);
    
    void deleteProductById(Integer productId); 
}