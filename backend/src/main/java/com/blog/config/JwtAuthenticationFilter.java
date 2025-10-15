package com.blog.config;

import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.blog.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("=== JWT FILTER CALLED ===");
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Request Method: " + req.getMethod());

        // Skip filter for public endpoints
        String requestPath = req.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            System.out.println("Public endpoint, skipping JWT validation");
            filterChain.doFilter(req, res);
            return;
        }

        String userName = null;
        String token = null;
        final String authHeader = req.getHeader("Authorization");

        System.out.println("Processing request: " + requestPath);
        System.out.println("Auth header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userName = jwtUtils.extractUsername(token);
                System.out.println("Extracted username: " + userName);
            } catch (Exception e) {
                System.out.println("Failed to extract username: " + e.getMessage());
                // Invalid token format - let Spring Security handle it
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                if (jwtUtils.validateToken(token, userDetails)) {
                    System.out.println("Token is valid for user: " + userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("Token validation failed for user: " + userName);
                    // Invalid token - let Spring Security handle the 401 response
                }
            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
                // Error during authentication - let Spring Security handle it
            }
        }

        // Always proceed to next filter - Spring Security will handle authorization
        filterChain.doFilter(req, res);
    }

    /**
     * Check if the request path is a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/uploads/");
    }
}