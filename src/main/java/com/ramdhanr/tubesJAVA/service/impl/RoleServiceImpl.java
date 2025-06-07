package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.model.Role;
import com.ramdhanr.tubesJAVA.repository.RoleRepository;
import com.ramdhanr.tubesJAVA.service.RoleService;
// import org.springframework.beans.factory.annotation.Autowired; // Tidak perlu lagi jika hanya satu constructor
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override // <-- TAMBAHKAN IMPLEMENTASI INI
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}