package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.CheckoutFormDto;
import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.Order;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.CartService;
import com.ramdhanr.tubesJAVA.service.FileStorageService; 
import com.ramdhanr.tubesJAVA.service.OrderService;
import com.ramdhanr.tubesJAVA.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.multipart.MultipartFile;   
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final FileStorageService fileStorageService; 

    public OrderController(OrderService orderService,
                           CartService cartService,
                           UserService userService,
                           FileStorageService fileStorageService) { 
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.fileStorageService = fileStorageService; 
    }

    // Metode showCheckoutPage() dan placeOrder()
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model,
                                   @AuthenticationPrincipal UserDetails currentUserDetails,
                                   RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melanjutkan ke checkout.");
            return "redirect:/login";
        }
        String username = currentUserDetails.getUsername();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Keranjang belanja Anda kosong. Silakan tambahkan produk terlebih dahulu.");
            return "redirect:/cart";
        }
        BigDecimal totalCartPrice = cartService.getTotalCartPrice(user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalCartPrice", totalCartPrice);
        if (!model.containsAttribute("checkoutFormDto")) {
            String defaultNameToUseInForm = user.getUsername(); 
            model.addAttribute("checkoutFormDto", 
            new CheckoutFormDto(defaultNameToUseInForm, "", "", ""));
        }
        model.addAttribute("title", "Checkout Pesanan");
        return "checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@ModelAttribute("checkoutFormDto") CheckoutFormDto checkoutFormDto,
                             @AuthenticationPrincipal UserDetails currentUserDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi Anda berakhir. Silakan login kembali.");
            return "redirect:/login";
        }
        String username = currentUserDetails.getUsername();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Keranjang belanja Anda kosong. Tidak bisa melanjutkan pesanan.");
            return "redirect:/cart";
        }
        try {
            Order placedOrder = orderService.placeOrder(user, cartItems, checkoutFormDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Pesanan Anda dengan ID #" + placedOrder.getId() + " berhasil dibuat! Silakan lanjutkan pembayaran.");
            return "redirect:/order/confirmation/" + placedOrder.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal membuat pesanan: " + e.getMessage());
            return "redirect:/checkout";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal membuat pesanan: " + e.getMessage());
            return "redirect:/checkout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat memproses pesanan.");
            return "redirect:/checkout";
        }
    }
    
    // Metode showOrderConfirmationPage() 
    @GetMapping("/confirmation/{orderId}")
    public String showOrderConfirmationPage(@PathVariable("orderId") Integer orderId,
                                            Model model,
                                            @AuthenticationPrincipal UserDetails currentUserDetails,
                                            RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melihat detail pesanan.");
            return "redirect:/login";
        }
        String username = currentUserDetails.getUsername();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));
        Optional<Order> orderOptional = orderService.findOrderByIdForUser(orderId, user);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pesanan dengan ID " + orderId + " tidak ditemukan atau bukan milik Anda.");
            return "redirect:/dashboard-user";
        }
        model.addAttribute("order", orderOptional.get());
        model.addAttribute("title", "Konfirmasi Pesanan #" + orderOptional.get().getId());
        return "order-confirmation";
    }

    // UNTUK MENANGANI UPLOAD BUKTI PEMBAYARAN
    @PostMapping("/{orderId}/upload-proof")
    public String handlePaymentProofUpload(@PathVariable("orderId") Integer orderId,
                                           @RequestParam("paymentProofFile") MultipartFile paymentProofFile,
                                           @AuthenticationPrincipal UserDetails currentUserDetails,
                                           RedirectAttributes redirectAttributes) {

        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk mengupload bukti pembayaran.");
            return "redirect:/login";
        }
        if (paymentProofFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "File bukti pembayaran tidak boleh kosong.");
            return "redirect:/order/confirmation/" + orderId;
        }

        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            // 1. Simpan file menggunakan FileStorageService
            // Kita simpan di subdirektori "payment_proofs"
            String filePath = fileStorageService.storeFile(paymentProofFile, "payment_proofs");

            // 2. Update order dengan URL bukti pembayaran dan status baru
            orderService.savePaymentProof(orderId, filePath, user); 

            redirectAttributes.addFlashAttribute("successMessage", "Bukti pembayaran berhasil diupload. Pesanan Anda sedang diproses.");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupload bukti pembayaran: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat mengupload bukti pembayaran.");
            
        }

        return "redirect:/order/confirmation/" + orderId; // Kembali ke halaman konfirmasi order
    }

    @GetMapping("/history")
    public String showOrderHistoryPage(Model model,
                                       @AuthenticationPrincipal UserDetails currentUserDetails,
                                       RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melihat riwayat pesanan.");
            return "redirect:/login";
        }

        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            List<Order> userOrders = orderService.getOrdersForUser(user); // Mengambil semua order milik user
            model.addAttribute("orders", userOrders);
            model.addAttribute("title", "Riwayat Pesanan Saya");

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Gagal memuat riwayat pesanan: " + e.getMessage());
            model.addAttribute("orders", Collections.emptyList()); 
        }
        return "order-history"; 
    }
}
