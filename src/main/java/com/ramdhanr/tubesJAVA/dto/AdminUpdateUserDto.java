package com.ramdhanr.tubesJAVA.dto;

// DTO untuk update user oleh admin

public record AdminUpdateUserDto(
    // Integer id, // Opsional, tergantung bagaimana kita handle di controller
    String username,
    String email,
    Integer roleId
) {
}