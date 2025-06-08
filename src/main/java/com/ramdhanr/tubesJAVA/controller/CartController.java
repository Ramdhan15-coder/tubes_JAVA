package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.CartService;
import com.ramdhanr.tubesJAVA.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections; 

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            @AuthenticationPrincipal UserDetails currentUserDetails,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk menambahkan produk ke keranjang.");
            return "redirect:/login";
        }

        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            cartService.addProductToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil ditambahkan ke keranjang!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menambahkan produk: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menambahkan produk: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat menambahkan ke keranjang.");
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty() && !referer.contains("/login") && !referer.contains("/register")) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard-user";
    }

    @GetMapping
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails currentUserDetails, RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melihat keranjang.");
            return "redirect:/login";
        }
        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            List<CartItem> cartItems = cartService.getCartItems(user);
            BigDecimal totalCartPrice = cartService.getTotalCartPrice(user);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalCartPrice", totalCartPrice);
            model.addAttribute("title", "Keranjang Belanja Anda");

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Gagal memuat keranjang: " + e.getMessage());
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("totalCartPrice", BigDecimal.ZERO);
        }
        return "cart";
    }

    // Metode untuk mengupdate kuantitas item di keranjang
    @PostMapping("/update")
    public String updateCartItemQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                         @RequestParam("quantity") int quantity,
                                         @AuthenticationPrincipal UserDetails currentUserDetails,
                                         RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi Anda berakhir. Silakan login kembali.");
            return "redirect:/login";
        }
        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            cartService.updateCartItemQuantity(user, cartItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Kuantitas item berhasil diupdate.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupdate kuantitas: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupdate kuantitas: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat mengupdate kuantitas.");
        }
        return "redirect:/cart";
    }

    // Metode untuk menghapus item dari keranjang
    @PostMapping("/remove")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId,
                                 @AuthenticationPrincipal UserDetails currentUserDetails,
                                 RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi Anda berakhir. Silakan login kembali.");
            return "redirect:/login";
        }
        try {
            String username = currentUserDetails.getUsername();
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User '" + username + "' tidak ditemukan."));

            cartService.removeCartItem(user, cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item berhasil dihapus dari keranjang.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus item: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan tidak terduga saat menghapus item.");
        }
        return "redirect:/cart";
    }
}