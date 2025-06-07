package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER: role langsung di-load saat User di-load
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Implementasi UserDetails dari Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MODIFIKASI DI SINI: Tambahkan prefix "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
    // Sebelumnya: return email;
    return this.username; // Diubah menjadi mengembalikan field username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Anggap akun tidak pernah expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Anggap akun tidak pernah terkunci
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Anggap kredensial tidak pernah expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Anggap akun selalu aktif
    }
}