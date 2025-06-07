package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.AdminReviewUpdateDto;
import com.ramdhanr.tubesJAVA.model.Review;
import com.ramdhanr.tubesJAVA.service.ReviewService;
// import org.springframework.dao.DataIntegrityViolationException; // Tidak kita tangkap secara spesifik di sini
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
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Metode listAllReviews() tetap sama
    @GetMapping
    public String listAllReviews(Model model) {
        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("reviews", allReviews);
        return "admin/reviews/list-reviews";
    }

    // Metode deleteReview() tetap sama
    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Integer reviewId, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReviewById(reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Review dengan ID " + reviewId + " berhasil dihapus.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan umum saat menghapus review ID " + reviewId + ".");
        }
        return "redirect:/admin/reviews";
    }

    // Metode showEditReviewForm() tetap sama
    @GetMapping("/edit/{reviewId}")
    public String showEditReviewForm(@PathVariable("reviewId") Integer reviewId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Review dengan ID " + reviewId + " tidak ditemukan.");
            return "redirect:/admin/reviews";
        }

        Review review = reviewOptional.get();
        if (!model.containsAttribute("adminReviewUpdateDto")) { // Hanya set jika belum ada (misal dari error redirect)
            AdminReviewUpdateDto reviewUpdateDto = new AdminReviewUpdateDto(
                    review.getRating(),
                    review.getComment()
            );
            model.addAttribute("adminReviewUpdateDto", reviewUpdateDto);
        }
        model.addAttribute("review", review);
        model.addAttribute("reviewId", reviewId);
        return "admin/reviews/edit-review";
    }

    // METODE BARU UNTUK MEMPROSES SUBMIT FORM EDIT REVIEW
    @PostMapping("/update/{reviewId}")
    public String processUpdateReviewForm(@PathVariable("reviewId") Integer reviewId,
                                          @ModelAttribute("adminReviewUpdateDto") AdminReviewUpdateDto reviewUpdateDto,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            Review updatedReview = reviewService.updateReviewByAdmin(reviewId, reviewUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Review ID " + updatedReview.getReviewId() + " berhasil diupdate!");
            return "redirect:/admin/reviews"; // Redirect ke halaman daftar review
        } catch (RuntimeException e) { // Menangkap error dari service (misal: Review tidak ditemukan saat update)
            model.addAttribute("error", e.getMessage()); // Kirim pesan error ke view
            // Kirim kembali DTO yang sudah diisi pengguna
            model.addAttribute("adminReviewUpdateDto", reviewUpdateDto);
            model.addAttribute("reviewId", reviewId); // Kirim kembali reviewId

            // Kita juga butuh objek review asli lagi untuk menampilkan info kontekstual di form edit
            Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);
            reviewOptional.ifPresent(review -> model.addAttribute("review", review));
            // Jika reviewOptional kosong (seharusnya tidak terjadi jika update gagal karena error validasi,
            // tapi bisa terjadi jika review dihapus orang lain di antara GET dan POST),
            // halaman edit mungkin akan kekurangan info kontekstual "review".
            // Idealnya, jika reviewOptional kosong di sini, kita redirect ke list dengan error.
            if(reviewOptional.isEmpty()){
                 redirectAttributes.addFlashAttribute("errorMessage", "Review dengan ID " + reviewId + " tidak ditemukan saat mencoba update.");
                 return "redirect:/admin/reviews";
            }

            return "admin/reviews/edit-review"; // Kembali ke form edit review untuk menampilkan error
        }
    }
}