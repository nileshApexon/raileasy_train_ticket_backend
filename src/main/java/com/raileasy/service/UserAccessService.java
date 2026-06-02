package com.raileasy.service;

import com.raileasy.common.security.JwtUserPrincipal;
import com.raileasy.domain.User;
import com.raileasy.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserAccessService {

    private final UserRepository userRepository;

    public UserAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the authenticated user from the security context (JWT token).
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof JwtUserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
        }

        JwtUserPrincipal jwtPrincipal = (JwtUserPrincipal) principal;
        UUID userId = jwtPrincipal.getUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Get the authenticated user and verify they have admin privileges.
     */
    public User requireAdmin() {
        User user = getAuthenticatedUser();
        if (!user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        return user;
    }

    /**
     * Check if the current user is authenticated (has valid JWT token).
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof JwtUserPrincipal;
    }
}

