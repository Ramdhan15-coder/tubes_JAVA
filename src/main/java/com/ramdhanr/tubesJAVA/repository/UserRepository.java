package com.ramdhanr.tubesJAVA.repository;

import com.ramdhanr.tubesJAVA.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORT INI
import org.springframework.data.repository.query.Param; // <-- IMPORT INI
import org.springframework.stereotype.Repository;

import java.util.List; // <-- IMPORT INI
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // ... (metode findByEmail, findByUsername, existsBy... tetap ada) ...
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND " +
           "(:roleId IS NULL OR u.role.id = :roleId)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("roleId") Integer roleId);
}