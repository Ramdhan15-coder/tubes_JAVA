package com.ramdhanr.tubesJAVA.service;

import com.ramdhanr.tubesJAVA.dto.AdminCreateUserDto;
import com.ramdhanr.tubesJAVA.dto.AdminUpdateUserDto; // <-- IMPORT DTO UPDATE
import com.ramdhanr.tubesJAVA.dto.RegisterDto;
import com.ramdhanr.tubesJAVA.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User registerUser(RegisterDto registerDto);

    List<User> getAllUsers();

    User createUserByAdmin(AdminCreateUserDto createUserDto);

    Optional<User> findUserById(Integer userId); 

    User updateUserByAdmin(Integer userId, AdminUpdateUserDto updateUserDto); 

    void deleteUserById(Integer userId);
    
    Optional<User> findUserByUsername(String username);

    List<User> searchUsers(String keyword, Integer roleId);







}