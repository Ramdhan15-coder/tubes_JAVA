package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.model.Role;
import java.util.Optional;

import java.util.List; // Jangan lupa import List


public interface RoleService {
    Optional<Role> findByName(String name);
    List<Role> getAllRoles(); // <-- TAMBAHKAN INI
}