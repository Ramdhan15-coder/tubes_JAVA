package com.ramdhanr.tubesJAVA.dto;

// Bisa pakai record untuk DTO yang immutable dan ringkas
// Anotasi validasi seperti @Min, @Max, @NotBlank bisa ditambahkan di sini nanti
public record ReviewDto(
    Byte rating, // Atau Integer, Byte cocok untuk 1-5
    String comment
) {
}