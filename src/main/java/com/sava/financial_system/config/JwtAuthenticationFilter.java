package com.sava.financial_system.config;

import com.sava.financial_system.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tok = extractTokenFromHeader(req);

            if (tok != null && jwtTokenProvider.validateToken(tok)) {
                String userId = jwtTokenProvider.getUserIdFromToken(tok).toString();
                String role = jwtTokenProvider.getRoleFromToken(tok);
                String email = jwtTokenProvider.getEmailFromToken(tok);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            System.err.println("Cannot set user authentication: " + ex.getMessage());
        }

        filterChain.doFilter(req, res);
    }

    private String extractTokenFromHeader(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
            return header.substring(7);

        return null;
    }
}
