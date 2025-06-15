package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.PasswordChangeDto;
import com.ramdhanr.tubesJAVA.dto.ProfileUpdateDto;
import com.ramdhanr.tubesJAVA.dto.RegisterDto;
import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.CartItem; // Diperlukan untuk metode yang berhubungan dengan CartService jika ada
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.model.Review;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.CartService; // Diperlukan jika ada logika cart di sini
import com.ramdhanr.tubesJAVA.service.ProductService;
import com.ramdhanr.tubesJAVA.service.ReviewService;
import com.ramdhanr.tubesJAVA.service.UserService;
import java.math.BigDecimal; // Diperlukan untuk DTO
import java.util.Collections; // Diperlukan untuk List kosong
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import untuk @AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails;       // Import untuk UserDetails
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class PageController {

    private final UserService userService;
    private final ProductService productService;
    private final ReviewService reviewService;
    private final CartService cartService; // Ditambahkan agar konsisten dengan GlobalControllerAdvice

    // Constructor diperbarui untuk semua service
    public PageController(UserService userService, ProductService productService, ReviewService reviewService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.reviewService = reviewService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/home")
    public String showHomePageAlternative() {
        return "home";
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
        return "product-detail";
    }

    @GetMapping("/dashboard-admin")
    public String showAdminDashboard() {
        return "dashboard_admin";
    }

    // --- METODE BARU UNTUK FITUR PROFIL ---

    // Menampilkan Halaman Profil (yang sudah ada form edit & ganti password)
    @GetMapping("/profile")
    public String showProfilePage(Model model, @AuthenticationPrincipal UserDetails currentUserDetails, RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melihat profil.");
            return "redirect:/login";
        }

        String username = currentUserDetails.getUsername();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

        // Siapkan DTO untuk form edit profil
        if (!model.containsAttribute("profileUpdateDto")) {
             model.addAttribute("profileUpdateDto", new ProfileUpdateDto(user.getUsername(), user.getEmail()));
        }
        // Siapkan DTO kosong untuk form ganti password
        if (!model.containsAttribute("passwordChangeDto")) {
            model.addAttribute("passwordChangeDto", new PasswordChangeDto("", "", ""));
        }

        model.addAttribute("user", user); // Objek user asli untuk info tambahan jika perlu
        model.addAttribute("title", "Profil Saya");
        
        return "profile"; // Path ke file templates/profile.html
    }

    // Memproses Form Edit Profil
    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute("profileUpdateDto") ProfileUpdateDto profileUpdateDto,
                                    @AuthenticationPrincipal UserDetails currentUserDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(currentUserDetails.getUsername(), profileUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profil berhasil diupdate!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal update profil: " + e.getMessage());
            // Kirim kembali DTO yang error agar form bisa terisi kembali
            redirectAttributes.addFlashAttribute("profileUpdateDto", profileUpdateDto);
        }
        return "redirect:/profile";
    }

    // Memproses Form Ganti Password
    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("passwordChangeDto") PasswordChangeDto passwordChangeDto,
                                 @AuthenticationPrincipal UserDetails currentUserDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserPassword(currentUserDetails.getUsername(), passwordChangeDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah! Silakan login kembali untuk keamanan.");
            return "redirect:/logout"; // Redirect ke logout setelah ganti password
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal ganti password: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}