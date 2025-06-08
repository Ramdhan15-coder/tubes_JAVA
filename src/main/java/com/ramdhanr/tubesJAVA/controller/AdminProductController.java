package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.AdminProductCreateDto;
import com.ramdhanr.tubesJAVA.dto.AdminProductUpdateDto;
import com.ramdhanr.tubesJAVA.model.Category;
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.service.CategoryService;
import com.ramdhanr.tubesJAVA.service.ProductService;
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {

        List<Product> productList;
        if (keyword != null && !keyword.trim().isEmpty()) {

            productList = productService.searchProducts(keyword);
        } else {
            
            productList = productService.getAllProducts();
        }

        model.addAttribute("products", productList);
        model.addAttribute("keyword", keyword); 

        return "admin/products/list-products";
    }

    // Metode showCreateProductForm()
    @GetMapping("/new")
    public String showCreateProductForm(Model model) {
        if (!model.containsAttribute("adminProductCreateDto")) {
            model.addAttribute("adminProductCreateDto",
                    new AdminProductCreateDto("", "", new BigDecimal("0.00"), 0, null, ""));
        }
        List<Category> availableCategories = categoryService.getAllCategories();
        model.addAttribute("availableCategories", availableCategories);
        return "admin/products/create-product";
    }

    // Metode processCreateProductForm()
    @PostMapping("/save")
    public String processCreateProductForm(@ModelAttribute("adminProductCreateDto") AdminProductCreateDto productCreateDto,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            Product newProduct = productService.createProduct(productCreateDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Produk '" + newProduct.getName() + "' berhasil ditambahkan!");
            return "redirect:/admin/products";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("adminProductCreateDto", productCreateDto);
            List<Category> availableCategories = categoryService.getAllCategories();
            model.addAttribute("availableCategories", availableCategories);
            return "admin/products/create-product";
        }
    }

    // Metode showEditProductForm()
    @GetMapping("/edit/{productId}")
    public String showEditProductForm(@PathVariable("productId") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.findProductById(productId);
        if (productOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Produk dengan ID " + productId + " tidak ditemukan.");
            return "redirect:/admin/products";
        }
        Product product = productOptional.get();
        if (!model.containsAttribute("adminProductUpdateDto")) {
            AdminProductUpdateDto productUpdateDto = new AdminProductUpdateDto(
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategory() != null ? product.getCategory().getId() : null,
                    product.getImageUrl()
            );
            model.addAttribute("adminProductUpdateDto", productUpdateDto);
        }
        model.addAttribute("productId", productId);
        List<Category> availableCategories = categoryService.getAllCategories();
        model.addAttribute("availableCategories", availableCategories);
        return "admin/products/edit-product";
    }

    // Metode processUpdateProductForm() 
    @PostMapping("/update/{productId}")
    public String processUpdateProductForm(@PathVariable("productId") Integer productId,
                                           @ModelAttribute("adminProductUpdateDto") AdminProductUpdateDto productUpdateDto,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            Product updatedProduct = productService.updateProduct(productId, productUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Produk '" + updatedProduct.getName() + "' berhasil diupdate!");
            return "redirect:/admin/products";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("adminProductUpdateDto", productUpdateDto);
            model.addAttribute("productId", productId);
            List<Category> availableCategories = categoryService.getAllCategories();
            model.addAttribute("availableCategories", availableCategories);
            return "admin/products/edit-product";
        }
    }

    //  UNTUK MEMPROSES PENGHAPUSAN PRODUK
    @GetMapping("/delete/{productId}") // Menghandle GET request dari link hapus
    public String deleteProduct(@PathVariable("productId") Integer productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Produk dengan ID " + productId + " berhasil dihapus.");
        } catch (DataIntegrityViolationException e) {
           
            redirectAttributes.addFlashAttribute("errorMessage",
                "Gagal menghapus produk ID " + productId + ". Produk ini mungkin sudah ada dalam pesanan atau data terkait lainnya.");
        } catch (RuntimeException e) {
           
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
           
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan umum saat menghapus produk ID " + productId + ".");
           
        }
        return "redirect:/admin/products"; 
    }
}