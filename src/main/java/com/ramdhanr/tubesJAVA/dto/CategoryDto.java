package com.ramdhanr.tubesJAVA.dto;

// Bisa pakai record (Java 17+) atau class biasa.
// Anotasi validasi @NotBlank bisa ditambahkan nanti.
public record CategoryDto(
    String name
) {
}