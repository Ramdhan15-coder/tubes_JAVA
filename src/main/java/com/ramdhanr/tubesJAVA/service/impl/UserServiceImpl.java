package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.dto.AdminCreateUserDto;
import com.ramdhanr.tubesJAVA.dto.AdminUpdateUserDto; // <-- IMPORT DTO UPDATE
import com.ramdhanr.tubesJAVA.dto.RegisterDto;
import com.ramdhanr.tubesJAVA.model.Role;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.repository.RoleRepository;
import com.ramdhanr.tubesJAVA.repository.UserRepository;
import com.ramdhanr.tubesJAVA.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional; 
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.security.core.Authentication;      
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ... (metode registerUser, loadUserByUsername, getAllUsers, createUserByAdmin, findUserById tetap sama) ...
    @Override
    @Transactional
    public User registerUser(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.username())) {
            throw new IllegalArgumentException("Error: Username '" + registerDto.username() + "' is already taken!");
        }
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new IllegalArgumentException("Error: Email '" + registerDto.email() + "' is already in use!");
        }
        User user = new User();
        user.setUsername(registerDto.username());
        user.setEmail(registerDto.email());
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Default role 'USER' is not found in the database. Please ensure it exists."));
        user.setRole(userRole);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUserByAdmin(AdminCreateUserDto createUserDto) {
        if (userRepository.existsByUsername(createUserDto.username())) {
            throw new IllegalArgumentException("Error: Username '" + createUserDto.username() + "' is already taken!");
        }
        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new IllegalArgumentException("Error: Email '" + createUserDto.email() + "' is already in use!");
        }
        User newUser = new User();
        newUser.setUsername(createUserDto.username());
        newUser.setEmail(createUserDto.email());
        newUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        Role assignedRole = roleRepository.findById(createUserDto.roleId())
                .orElseThrow(() -> new RuntimeException("Error: Role with ID " + createUserDto.roleId() + " not found."));
        newUser.setRole(assignedRole);
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    // IMPLEMENTASI METODE BARU UNTUK UPDATE USER BY ADMIN
    @Override
    @Transactional // Operasi ini mengubah data, jadi @Transactional
    public User updateUserByAdmin(Integer userId, AdminUpdateUserDto updateUserDto) {
        // 1. Ambil user yang akan diupdate dari database
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User with ID " + userId + " not found. Cannot update."));

        // 2. Validasi dan update username jika diubah
        // Cek apakah username baru berbeda DAN apakah username baru sudah dipakai user lain
        if (!userToUpdate.getUsername().equals(updateUserDto.username()) &&
            userRepository.existsByUsername(updateUserDto.username())) {
            throw new IllegalArgumentException("Error: Username '" + updateUserDto.username() + "' is already taken by another user!");
        }
        userToUpdate.setUsername(updateUserDto.username());

        // 3. Validasi dan update email jika diubah
        // Cek apakah email baru berbeda DAN apakah email baru sudah dipakai user lain
        if (!userToUpdate.getEmail().equals(updateUserDto.email()) &&
            userRepository.existsByEmail(updateUserDto.email())) {
            throw new IllegalArgumentException("Error: Email '" + updateUserDto.email() + "' is already in use by another user!");
        }
        userToUpdate.setEmail(updateUserDto.email());

        // 4. Update Role
        // Cek apakah roleId yang diberikan berbeda dengan roleId user saat ini
        if (!userToUpdate.getRole().getId().equals(updateUserDto.roleId())) {
            Role newRole = roleRepository.findById(updateUserDto.roleId())
                    .orElseThrow(() -> new RuntimeException("Error: Role with ID " + updateUserDto.roleId() + " not found."));
            userToUpdate.setRole(newRole);
        }
        // Catatan: Kita tidak mengubah password di sini. Jika admin ingin mengubah password user,
        // itu sebaiknya jadi fitur terpisah (misalnya "Reset Password").

        // 5. Simpan perubahan ke database
        return userRepository.save(userToUpdate);
    }

    @Override
    @Transactional // Operasi ini mengubah data
    public void deleteUserById(Integer userId) {
        // 1. Dapatkan informasi user yang sedang login (admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Admin not authenticated. Cannot perform delete operation.");
        }

        Object principal = authentication.getPrincipal();
        String currentAdminUsername;
        if (principal instanceof UserDetails) {
            currentAdminUsername = ((UserDetails) principal).getUsername();
        } else {
            currentAdminUsername = principal.toString();
        }

        User adminUser = userRepository.findByUsername(currentAdminUsername)
            .orElseThrow(() -> new RuntimeException("Authenticated admin user not found in database."));

        // 2. Cek apakah admin mencoba menghapus dirinya sendiri
        if (adminUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Error: Admin tidak dapat menghapus akun sendiri.");
        }

        // 3. Cek apakah user yang akan dihapus ada
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Error: User dengan ID " + userId + " tidak ditemukan. Tidak bisa dihapus.");
        }

        // 4. Hapus user
        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            
            throw new DataIntegrityViolationException("Error: User tidak dapat dihapus karena memiliki data terkait (misalnya pesanan). Hapus data terkait terlebih dahulu.", e);
        }
    }
        @Override
        @Transactional(readOnly = true)
        public Optional<User> findUserByUsername(String username) {
            return userRepository.findByUsername(username); // Menggunakan metode dari UserRepository
        }
}