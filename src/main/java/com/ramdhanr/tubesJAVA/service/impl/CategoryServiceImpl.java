package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.dto.CategoryDto; // <-- IMPORT DTO CATEGORY
import com.ramdhanr.tubesJAVA.model.Category;
import com.ramdhanr.tubesJAVA.repository.CategoryRepository;
import com.ramdhanr.tubesJAVA.service.CategoryService;
//import org.springframework.dao.DataIntegrityViolationException; // Untuk delete jika masih ada produk (jika ON DELETE RESTRICT)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(); // Bisa diurutkan jika perlu, misal: findAll(Sort.by("name"))
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name); // Asumsi metode ini ada di CategoryRepository
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        // Cek apakah nama kategori sudah ada (case-insensitive bisa dipertimbangkan)
        if (categoryRepository.findByName(categoryDto.name()).isPresent()) {
            throw new IllegalArgumentException("Error: Nama kategori '" + categoryDto.name() + "' sudah ada.");
        }
        Category category = new Category();
        category.setName(categoryDto.name());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Integer categoryId, CategoryDto categoryDto) {
        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Error: Kategori dengan ID " + categoryId + " tidak ditemukan."));

        // Cek apakah nama kategori baru (jika diubah) sudah ada dan bukan kategori ini sendiri
        Optional<Category> existingCategoryWithName = categoryRepository.findByName(categoryDto.name());
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Error: Nama kategori '" + categoryDto.name() + "' sudah digunakan oleh kategori lain.");
        }

        categoryToUpdate.setName(categoryDto.name());
        return categoryRepository.save(categoryToUpdate);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Error: Kategori dengan ID " + categoryId + " tidak ditemukan.");
        }
        // Karena ada ON DELETE SET NULL di tabel products,
        // kita tidak perlu khawatir DataIntegrityViolationException dari sisi products.
        // Namun, jika ada constraint lain, perlu ditangani.
        try {
            categoryRepository.deleteById(categoryId);
        } catch (Exception e) {
            // Catch exception umum jika ada masalah lain saat delete
            throw new RuntimeException("Error: Gagal menghapus kategori ID " + categoryId + ". " + e.getMessage(), e);
        }
    }
}