package com.ramdhanr.tubesJAVA.dto;

// DTO untuk membawa data dari form ganti password
public record PasswordChangeDto(
    String currentPassword,
    String newPassword,
    String confirmNewPassword
) {
}