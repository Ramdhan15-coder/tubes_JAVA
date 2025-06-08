package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.RegisterDto;
import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.model.Review;
import com.ramdhanr.tubesJAVA.service.ProductService;
import com.ramdhanr.tubesJAVA.service.ReviewService;
import com.ramdhanr.tubesJAVA.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    private final UserService userService;
    private final ProductService productService;
    private final ReviewService reviewService;

    // Constructor untuk dependency injection
    public PageController(UserService userService, ProductService productService, ReviewService reviewService) {
        this.userService = userService;
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "home"; // Merender templates/home.html
    }

    @GetMapping("/home")
    public String showHomePageAlternative() {
        return "home"; // Merender templates/home.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        
        return "login"; 
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto("", "", ""));
        }
        return "register"; 
    }

    @PostMapping("/do_register")
    public String processRegistration(@ModelAttribute("registerDto") RegisterDto registerDto,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute("registered", true);
            return "redirect:/login"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDto", registerDto); 
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Terjadi kesalahan sistem saat registrasi. Silakan coba lagi.");
            model.addAttribute("registerDto", registerDto);
            return "register";
        }
    }

    @GetMapping("/dashboard-user")
    public String showUserDashboard(Model model) {
        List<Product> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        return "dashboard_user"; 
    }

    @GetMapping("/products/{productId}")
    public String showProductDetailPage(@PathVariable("productId") Integer productId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.findProductById(productId);

        if (productOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Produk dengan ID " + productId + " tidak ditemukan.");
            return "redirect:/dashboard-user"; 
        }

        Product product = productOptional.get();
        List<Review> reviews = reviewService.getReviewsByProductId(productId);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
       
        if (!model.containsAttribute("newReview")) { 
             model.addAttribute("newReview", new ReviewDto(null, ""));
        }
        return "product-detail"; // Merender templates/product-detail.html
    }

    // METODE UNTUK MENAMPILKAN DASHBOARD ADMIN
    @GetMapping("/dashboard-admin")
    public String showAdminDashboard() {
      
        return "dashboard_admin"; // Merender templates/dashboard_admin.html
    }
}