package com.ramdhanr.tubesJAVA.dto;

import java.math.BigDecimal;

public record AdminProductCreateDto(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Integer categoryId, // ID dari kategori yang dipilih
        String imageUrl      // Path ke gambar, misal: /images/sepatu_baru.jpg
) {
}