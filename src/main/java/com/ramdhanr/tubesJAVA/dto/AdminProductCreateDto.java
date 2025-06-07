package com.ramdhanr.tubesJAVA.dto;

import java.math.BigDecimal;

// Menggunakan record untuk DTO (Java 17 mendukung ini)
// Anotasi validasi bisa ditambahkan nanti jika perlu (@NotBlank, @NotNull, @Min, dll.)
public record AdminProductCreateDto(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Integer categoryId, // ID dari kategori yang dipilih
        String imageUrl      // Path ke gambar, misal: /images/sepatu_baru.jpg
) {
}