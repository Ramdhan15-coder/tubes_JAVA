package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.dto.AdminProductCreateDto;
import com.ramdhanr.tubesJAVA.dto.AdminProductUpdateDto;
import com.ramdhanr.tubesJAVA.model.Category;
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.repository.ProductRepository;
import com.ramdhanr.tubesJAVA.service.CategoryService;
import com.ramdhanr.tubesJAVA.service.ProductService;
import org.springframework.dao.DataIntegrityViolationException; // <-- IMPORT INI
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    // ... (metode getAllProducts, findProductById, createProduct, updateProduct tetap sama) ...
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional
    public Product createProduct(AdminProductCreateDto productCreateDto) {
        Product newProduct = new Product();
        newProduct.setName(productCreateDto.name());
        newProduct.setDescription(productCreateDto.description());
        newProduct.setPrice(productCreateDto.price());
        newProduct.setStock(productCreateDto.stock());
        newProduct.setImageUrl(productCreateDto.imageUrl());
        if (productCreateDto.categoryId() != null) {
            Category productCategory = categoryService.findCategoryById(productCreateDto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Error: Kategori dengan ID " + productCreateDto.categoryId() + " tidak ditemukan."));
            newProduct.setCategory(productCategory);
        } else {
            newProduct.setCategory(null);
        }
        return productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer productId, AdminProductUpdateDto productUpdateDto) {
        Product productToUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Error: Produk dengan ID " + productId + " tidak ditemukan. Update gagal."));
        productToUpdate.setName(productUpdateDto.name());
        productToUpdate.setDescription(productUpdateDto.description());
        productToUpdate.setPrice(productUpdateDto.price());
        productToUpdate.setStock(productUpdateDto.stock());
        productToUpdate.setImageUrl(productUpdateDto.imageUrl());
        if (productUpdateDto.categoryId() != null) {
            if (productToUpdate.getCategory() == null || !productToUpdate.getCategory().getId().equals(productUpdateDto.categoryId())) {
                Category newCategory = categoryService.findCategoryById(productUpdateDto.categoryId())
                        .orElseThrow(() -> new RuntimeException("Error: Kategori dengan ID " + productUpdateDto.categoryId() + " tidak ditemukan."));
                productToUpdate.setCategory(newCategory);
            }
        } else {
            productToUpdate.setCategory(null);
        }
        return productRepository.save(productToUpdate);
    }

    // IMPLEMENTASI METODE BARU UNTUK DELETE PRODUCT BY ID
    @Override
    @Transactional // Operasi ini mengubah data
    public void deleteProductById(Integer productId) {
        // 1. Cek apakah produk yang akan dihapus ada
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Error: Produk dengan ID " + productId + " tidak ditemukan. Tidak bisa dihapus.");
        }

        // 2. Hapus produk
        try {
            productRepository.deleteById(productId);
        } catch (DataIntegrityViolationException e) {
            // Ini terjadi jika produk memiliki data terkait di tabel lain yang mencegah penghapusan
            // (misalnya, produk ini ada di order_items dan foreign key-nya ON DELETE RESTRICT)
            // Kita perlu memberikan pesan error yang lebih jelas kepada admin.
            throw new DataIntegrityViolationException(
                "Error: Produk tidak dapat dihapus karena sudah digunakan di data lain (misalnya, dalam pesanan). " +
                "Hapus atau ubah data terkait tersebut terlebih dahulu.", e);
        }
    }

        @Override
    @Transactional
    public void decreaseStock(Integer productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Produk dengan ID " + productId + " tidak ditemukan untuk update stok."));
        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("Stok produk '" + product.getName() + "' tidak mencukupi untuk dikurangi sebanyak " + quantity + ".");
        }
        product.setStock(newStock);
        productRepository.save(product);
    }
}