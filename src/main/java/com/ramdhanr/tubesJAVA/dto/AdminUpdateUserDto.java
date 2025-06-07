package com.ramdhanr.tubesJAVA.dto;

// DTO untuk update user oleh admin
// Kita akan butuh ID user juga, tapi biasanya ID didapat dari PathVariable,
// jadi tidak perlu dimasukkan ke DTO yang dikirim dari form secara eksplisit,
// atau bisa juga dimasukkan sebagai hidden field jika diperlukan.
// Untuk DTO ini, kita fokus pada field yang bisa diubah.
public record AdminUpdateUserDto(
    // Integer id, // Opsional, tergantung bagaimana kita handle di controller
    String username,
    String email,
    Integer roleId
) {
}