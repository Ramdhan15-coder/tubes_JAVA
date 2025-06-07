package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.LoginDto;
import com.ramdhanr.tubesJAVA.dto.RegisterDto;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            User registeredUser = userService.registerUser(registerDto);
            // Hindari mengirim password kembali ke klien
            // Anda bisa membuat UserResponseDto jika perlu mengirim detail user
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("userId", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            response.put("email", registeredUser.getEmail());
            response.put("role", registeredUser.getRole().getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            // Menangkap RuntimeException dari Role not found
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.username(),
                            loginDto.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Jika menggunakan JWT, token akan di-generate dan dikembalikan di sini.
            // Untuk sekarang, kita kembalikan pesan sukses dan detail user sederhana.
            User user = (User) authentication.getPrincipal(); // User kita yg implement UserDetails

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User logged in successfully!");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail()); // dari getUsername() di UserDetails yg kita override
            response.put("role", user.getRole().getName());
            // Jika menggunakan session-based auth (default dengan formLogin),
            // Spring Security akan handle session cookie.

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Tangani jika autentikasi gagal (misal: BadCredentialsException)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }
}