package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.ReviewService;
import com.ramdhanr.tubesJAVA.service.UserService; // Untuk mengambil objek User dari UserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products/{productId}/reviews") // Base path untuk review terkait produk
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService; // Untuk mengambil entitas User berdasarkan username

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // Metode untuk memproses submit form review baru
    @PostMapping // Akan menghandle POST request ke /products/{productId}/reviews
    public String submitReview(@PathVariable("productId") Integer productId,
                               @ModelAttribute("newReview") ReviewDto reviewDto,
                               @AuthenticationPrincipal UserDetails currentUserDetails, // Mengambil user yang sedang login
                               RedirectAttributes redirectAttributes) {

        if (currentUserDetails == null) {
            // Seharusnya tidak terjadi jika endpoint diproteksi dengan benar,
            // tapi sebagai fallback atau jika ada akses anonim yang tidak diinginkan.
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk memberikan review.");
            return "redirect:/login";
        }

        try {
            // Ambil username dari UserDetails, lalu ambil entitas User lengkap dari UserService
            // Kita butuh entitas User untuk relasi di Review, bukan hanya UserDetails.
            String username = currentUserDetails.getUsername();
            User currentUser = userService.findUserByUsername(username) // Asumsi ada metode ini di UserService
                .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan untuk memberikan review."));

            reviewService.saveReview(reviewDto, productId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Review Anda berhasil ditambahkan!");

        } catch (IllegalArgumentException e) { // Misal jika user sudah pernah review (jika logika itu diaktifkan)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) { // Misal produk tidak ditemukan, atau user tidak ditemukan
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menyimpan review: " + e.getMessage());
        } catch (Exception e) { // Penampung error umum lainnya
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat menyimpan review.");
        }

        return "redirect:/products/" + productId; // Redirect kembali ke halaman detail produk yang sama
    }
}