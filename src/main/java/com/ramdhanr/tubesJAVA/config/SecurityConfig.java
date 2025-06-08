package com.ramdhanr.tubesJAVA.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // Constructor sekarang hanya menerima AuthenticationSuccessHandler
    public SecurityConfig(AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Proteksi CSRF tetap aktif secara default
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                        "/", "/home", "/register", "/do_register", "/login", "/perform_login",
                        "/css/**", "/js/**", "/images/**", "/uploads/**", // Izinkan akses ke static resources
                        "/api/auth/**"
                ).permitAll()
                .requestMatchers("/dashboard-user", "/cart/**", "/order/**").hasRole("USER") // Proteksi halaman user
                .requestMatchers("/admin/**", "/dashboard-admin/**").hasRole("ADMIN") // Proteksi halaman admin
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/login?error=true")
                // .permitAll() // Sudah tercakup di requestMatchers
            )
            // Konfigurasi logout yang lebih ringkas
            .logout(logout -> logout
                .logoutUrl("/logout") 
                .logoutSuccessUrl("/login?logout=true") 
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            
            ;

        return http.build();
    }
}