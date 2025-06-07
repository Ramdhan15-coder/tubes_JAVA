package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.dto.AdminCreateUserDto;
import com.ramdhanr.tubesJAVA.dto.AdminUpdateUserDto;
import com.ramdhanr.tubesJAVA.model.Role;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.service.RoleService;
import com.ramdhanr.tubesJAVA.service.UserService;
import org.springframework.dao.DataIntegrityViolationException; // <-- PASTIKAN IMPORT INI ADA
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Metode listUsers() tetap sama
    @GetMapping
    public String listUsers(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "admin/users/list-users";
    }

    // Metode showCreateUserForm() tetap sama
    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        if (!model.containsAttribute("adminCreateUserDto")) {
            model.addAttribute("adminCreateUserDto", new AdminCreateUserDto("", "", "", null));
        }
        List<Role> availableRoles = roleService.getAllRoles();
        model.addAttribute("availableRoles", availableRoles);
        return "admin/users/create-user";
    }

    // Metode processCreateUserForm() tetap sama
    @PostMapping("/save")
    public String processCreateUserForm(@ModelAttribute("adminCreateUserDto") AdminCreateUserDto createUserDto,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            User newUser = userService.createUserByAdmin(createUserDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User '" + newUser.getUsername() + "' berhasil ditambahkan!");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("adminCreateUserDto", createUserDto);
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "admin/users/create-user";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            model.addAttribute("adminCreateUserDto", createUserDto);
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "admin/users/create-user";
        }
    }

    // Metode showEditUserForm() tetap sama
    @GetMapping("/edit/{userId}")
    public String showEditUserForm(@PathVariable("userId") Integer userId, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User dengan ID " + userId + " tidak ditemukan.");
            return "redirect:/admin/users";
        }
        User user = userOptional.get();
        if (!model.containsAttribute("adminUpdateUserDto")) {
             AdminUpdateUserDto adminUpdateUserDto = new AdminUpdateUserDto(
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().getId()
            );
            model.addAttribute("adminUpdateUserDto", adminUpdateUserDto);
        }
        model.addAttribute("userId", userId);
        List<Role> availableRoles = roleService.getAllRoles();
        model.addAttribute("availableRoles", availableRoles);
        return "admin/users/edit-user";
    }

    // Metode processUpdateUserForm() tetap sama
    @PostMapping("/update/{userId}")
    public String processUpdateUserForm(@PathVariable("userId") Integer userId,
                                        @ModelAttribute("adminUpdateUserDto") AdminUpdateUserDto updateUserDto,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            User updatedUser = userService.updateUserByAdmin(userId, updateUserDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User '" + updatedUser.getUsername() + "' berhasil diupdate!");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("adminUpdateUserDto", updateUserDto);
            model.addAttribute("userId", userId);
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "admin/users/edit-user";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            model.addAttribute("adminUpdateUserDto", updateUserDto);
            model.addAttribute("userId", userId);
            List<Role> availableRoles = roleService.getAllRoles();
            model.addAttribute("availableRoles", availableRoles);
            return "admin/users/edit-user";
        }
    }

    /// MODIFIKASI DI SINI: Pisahkan catch block untuk deleteUser
    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User dengan ID " + userId + " berhasil dihapus.");
        } catch (IllegalArgumentException e) { // Tangani IllegalArgumentException (misal: self-delete)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataIntegrityViolationException e) { // Tangani DataIntegrityViolationException
            redirectAttributes.addFlashAttribute("errorMessage", "Error: User tidak dapat dihapus karena memiliki data terkait (misalnya pesanan). " + e.getMessage());
        } catch (RuntimeException e) { // Tangani RuntimeException lain (misal: user tidak ditemukan)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) { // Tangani error umum lainnya
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan umum saat menghapus user.");
        }
        return "redirect:/admin/users"; // Redirect kembali ke halaman daftar user
    }
}