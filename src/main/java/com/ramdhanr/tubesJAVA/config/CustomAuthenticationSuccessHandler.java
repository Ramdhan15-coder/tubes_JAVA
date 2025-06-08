package com.ramdhanr.tubesJAVA.config; // Sesuaikan jika ditaruh di package lain

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component // Daftarkan sebagai Spring Bean
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            System.out.println("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                return "/dashboard-admin"; // URL untuk dashboard admin
            } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                return "/dashboard-user"; // URL untuk dashboard user
            }
        }
        // Jika tidak ada role yang cocok (seharusnya tidak terjadi jika user punya role)
        
        throw new IllegalStateException("User has no suitable role for redirection.");
    }
}