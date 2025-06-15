package com.ramdhanr.tubesJAVA.dto;

// DTO untuk membawa data update profil (hanya email yang bisa diubah)
public record ProfileUpdateDto(
    String username, // Username ditampilkan tapi tidak bisa diubah (readonly)
    String email
) {
}