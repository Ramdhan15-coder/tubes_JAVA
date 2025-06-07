package com.ramdhanr.tubesJAVA.dto;

// Bisa menggunakan record (Java 16+) atau class biasa
// Untuk validasi, kita bisa tambahkan anotasi nanti (misal @NotBlank, @Email, @Size)
public record AdminCreateUserDto(
        String username,
        String email,
        String password,
        Integer roleId // ID dari role yang dipilih admin
) {
}