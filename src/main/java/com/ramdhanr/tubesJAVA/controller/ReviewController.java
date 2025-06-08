package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.ReviewDto;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.ReviewService;
import com.ramdhanr.tubesJAVA.service.UserService; 
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products/{productId}/reviews") 
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService; 

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // Metode untuk memproses submit form review baru
    @PostMapping 
    public String submitReview(@PathVariable("productId") Integer productId,
                               @ModelAttribute("newReview") ReviewDto reviewDto,
                               @AuthenticationPrincipal UserDetails currentUserDetails,
                               RedirectAttributes redirectAttributes) {

        if (currentUserDetails == null) {
           
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk memberikan review.");
            return "redirect:/login";
        }

        try {
    
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

        return "redirect:/products/" + productId; 
    }
}