package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.CategoryDto;
import com.ramdhanr.tubesJAVA.model.Category;
import com.ramdhanr.tubesJAVA.service.CategoryService;
//import org.springframework.dao.DataIntegrityViolationException; // Import jika ingin menangkap secara spesifik
import org.springframework.stereotype.Controller;
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

    // Menampilkan daftar semua kategori (sudah ada)
    @GetMapping
    public String listCategories(Model model) {
        List<Category> allCategories = categoryService.getAllCategories();
        model.addAttribute("categories", allCategories);
        return "admin/categories/list-categories";
    }

    // METODE BARU: Menampilkan form untuk membuat kategori baru
    @GetMapping("/new")
    public String showCreateCategoryForm(Model model) {
        // Hanya tambahkan DTO baru jika belum ada (misal dari error redirect)
        if (!model.containsAttribute("categoryDto")) {
            model.addAttribute("categoryDto", new CategoryDto("")); // DTO kosong untuk form
        }
        return "admin/categories/create-category";
    }

    // METODE BARU: Memproses penyimpanan kategori baru
    @PostMapping("/save")
    public String processCreateCategoryForm(@ModelAttribute("categoryDto") CategoryDto categoryDto,
                                            Model model, // Untuk error handling jika kembali ke form
                                            RedirectAttributes redirectAttributes) {
        try {
            Category newCategory = categoryService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Kategori '" + newCategory.getName() + "' berhasil ditambahkan!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) { // Misal nama kategori sudah ada
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categoryDto", categoryDto); // Kirim kembali DTO yg diinput
            return "admin/categories/create-category"; // Kembali ke form create
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan tak terduga saat menyimpan kategori.");
            model.addAttribute("categoryDto", categoryDto);
            return "admin/categories/create-category";
        }
    }

    // Menampilkan form edit kategori (sudah ada)
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

    // Memproses update kategori (sudah ada)
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

    // METODE BARU: Memproses penghapusan kategori
    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Integer categoryId, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(categoryId);
            redirectAttributes.addFlashAttribute("successMessage", "Kategori dengan ID " + categoryId + " berhasil dihapus.");
        } catch (RuntimeException e) { // Menangkap error dari service (misal: kategori tidak ditemukan)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan umum saat menghapus kategori ID " + categoryId + ".");
        }
        return "redirect:/admin/categories";
    }
}