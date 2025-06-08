package com.ramdhanr.tubesJAVA.dto;

// Kita bisa pakai record untuk DTO yang ringkas (Java 17 mendukung ini)

public record CheckoutFormDto(
    String fullName,        // Untuk Nama Lengkap Penerima
    String address,         // Untuk Alamat Lengkap Pengiriman
    String phoneNumber,     // Untuk Nomor Telepon Penerima
    String notes            // Untuk Catatan (Opsional)
) {
}