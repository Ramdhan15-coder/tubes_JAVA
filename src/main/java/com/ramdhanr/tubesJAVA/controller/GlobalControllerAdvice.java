package com.ramdhanr.tubesJAVA.controller; // Atau package lain yang sesuai dengan lokasimu

import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.CartService;
import com.ramdhanr.tubesJAVA.service.UserService;
// import org.springframework.beans.factory.annotation.Autowired; // Tidak diperlukan lagi di sini
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice // Berlaku untuk semua controller
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final UserService userService;

    // @Autowired // Anotasi ini dihapus karena tidak wajib jika hanya ada satu constructor
    public GlobalControllerAdvice(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    // Metode ini akan menambahkan 'cartItemCount' ke model untuk semua view
    @ModelAttribute("cartItemCount")
    public Integer getCartItemCount(@AuthenticationPrincipal UserDetails currentUserDetails) {
        if (currentUserDetails != null) {
            String username = currentUserDetails.getUsername();
            // Ambil entitas User berdasarkan username
            User user = userService.findUserByUsername(username).orElse(null);
            if (user != null) {
                List<CartItem> cartItems = cartService.getCartItems(user);
                return cartItems != null ? cartItems.size() : 0;
            }
        }
        return 0; // Default jika user tidak login atau tidak ada keranjang
    }
}