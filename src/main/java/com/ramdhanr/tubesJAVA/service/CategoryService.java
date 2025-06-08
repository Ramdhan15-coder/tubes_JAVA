package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.CategoryDto; // <-- IMPORT DTO CATEGORY
import com.ramdhanr.tubesJAVA.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAllCategories(); 

    Optional<Category> findCategoryById(Integer categoryId);

    Optional<Category> findByName(String name); 

    Category createCategory(CategoryDto categoryDto); 

    List<Category> searchCategories(String keyword);

    Category updateCategory(Integer categoryId, CategoryDto categoryDto); 

    void deleteCategoryById(Integer categoryId); 
}