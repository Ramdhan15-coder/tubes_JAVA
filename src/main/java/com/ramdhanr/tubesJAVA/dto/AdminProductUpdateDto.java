package com.ramdhanr.tubesJAVA.dto;

import java.math.BigDecimal;

// DTO untuk update produk oleh admin

public record AdminProductUpdateDto(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Integer categoryId, // ID dari kategori yang dipilih
        String imageUrl      // Path ke gambar, misal: /images/sepatu_lama_diedit.jpg
        // Password tidak relevan untuk produk
) {
}