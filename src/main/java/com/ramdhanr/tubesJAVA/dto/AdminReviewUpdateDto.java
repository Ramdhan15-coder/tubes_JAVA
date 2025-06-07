package com.ramdhanr.tubesJAVA.dto;

// DTO untuk update review oleh admin
public record AdminReviewUpdateDto(
    Byte rating, // Atau Integer, Byte cocok untuk 1-5
    String comment
    // User dan Produk tidak diubah oleh admin saat edit review
) {
}