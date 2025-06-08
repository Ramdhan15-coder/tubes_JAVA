package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.CategoryDto;
import com.ramdhanr.tubesJAVA.model.Category;
import com.ramdhanr.tubesJAVA.service.CategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

   
    @GetMapping
    public String listCategories(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {

        List<Category> categoryList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Jika ada keyword, lakukan search
            categoryList = categoryService.searchCategories(keyword);
        } else {
            // Jika tidak ada keyword, tampilkan semua kategori
            categoryList = categoryService.getAllCategories();
        }

        model.addAttribute("categories", categoryList);
        model.addAttribute("keyword", keyword); // Kirim kembali keyword ke view

        return "admin/categories/list-categories";
    }

    // METODE BARU: Menampilkan form untuk membuat kategori baru
    @GetMapping("/new")
    public String showCreateCategoryForm(Model model) {
        
        if (!model.containsAttribute("categoryDto")) {
            model.addAttribute("categoryDto", new CategoryDto("")); 
        }
        return "admin/categories/create-category";
    }

    // Memproses penyimpanan kategori baru
    @PostMapping("/save")
    public String processCreateCategoryForm(@ModelAttribute("categoryDto") CategoryDto categoryDto,
                                            Model model, // Untuk error handling jika kembali ke form
                                            RedirectAttributes redirectAttributes) {
        try {
            Category newCategory = categoryService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Kategori '" + newCategory.getName() + "' berhasil ditambahkan!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) { 
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categoryDto", categoryDto); 
            return "admin/categories/create-category"; 
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan tak terduga saat menyimpan kategori.");
            model.addAttribute("categoryDto", categoryDto);
            return "admin/categories/create-category";
        }
    }

    // Menampilkan form edit kategori
    @GetMapping("/edit/{categoryId}")
    public String showEditCategoryForm(@PathVariable("categoryId") Integer categoryId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOptional = categoryService.findCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategori dengan ID " + categoryId + " tidak ditemukan.");
            return "redirect:/admin/categories";
        }
        Category category = categoryOptional.get();
        if (!model.containsAttribute("categoryDto")) {
            model.addAttribute("categoryDto", new CategoryDto(category.getName()));
        }
        model.addAttribute("categoryId", categoryId);
        return "admin/categories/edit-category";
    }

    // Memproses update kategori 
    @PostMapping("/update/{categoryId}")
    public String processUpdateCategoryForm(@PathVariable("categoryId") Integer categoryId,
                                            @ModelAttribute("categoryDto") CategoryDto categoryDto,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, categoryDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Kategori '" + updatedCategory.getName() + "' (ID: " + categoryId + ") berhasil diupdate!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categoryDto", categoryDto);
            model.addAttribute("categoryId", categoryId);
            return "admin/categories/edit-category";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            model.addAttribute("categoryDto", categoryDto);
            model.addAttribute("categoryId", categoryId);
            return "admin/categories/edit-category";
        }
    }

    // Memproses penghapusan kategori
    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Integer categoryId, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(categoryId);
            redirectAttributes.addFlashAttribute("successMessage", "Kategori dengan ID " + categoryId + " berhasil dihapus.");
        } catch (RuntimeException e) { 
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan umum saat menghapus kategori ID " + categoryId + ".");
        }
        return "redirect:/admin/categories";
    }
}