package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.CategoryDto; // <-- IMPORT DTO CATEGORY
import com.ramdhanr.tubesJAVA.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAllCategories(); // Sudah ada

    Optional<Category> findCategoryById(Integer categoryId); // Sudah ada

    Optional<Category> findByName(String name); // <-- TAMBAHKAN (untuk validasi duplikat)

    Category createCategory(CategoryDto categoryDto); // <-- TAMBAHKAN

    Category updateCategory(Integer categoryId, CategoryDto categoryDto); // <-- TAMBAHKAN

    void deleteCategoryById(Integer categoryId); // <-- TAMBAHKAN
}