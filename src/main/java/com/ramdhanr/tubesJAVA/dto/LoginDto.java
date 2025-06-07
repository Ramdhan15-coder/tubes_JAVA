package com.ramdhanr.tubesJAVA.dto;

// Menggunakan record untuk DTO (Java 16+)
public record LoginDto(
        String username,
        String password
) {
}