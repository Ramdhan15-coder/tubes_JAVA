package com.ramdhanr.tubesJAVA.dto;


public record AdminCreateUserDto(
        String username,
        String email,
        String password,
        Integer roleId // ID dari role yang dipilih admin
) {
}