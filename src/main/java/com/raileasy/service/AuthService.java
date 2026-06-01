package com.raileasy.service;

import com.raileasy.domain.User;
import com.raileasy.dto.auth.AuthLoginRequestDto;
import com.raileasy.dto.auth.AuthRegisterRequestDto;
import com.raileasy.dto.auth.AuthResponseDto;
import com.raileasy.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {
        userRepository.findByEmailIgnoreCase(request.email().trim())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                });

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setName(request.name().trim());
        user.setAdmin(false);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return toAuthResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthLoginRequestDto request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!isPasswordValid(user, request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return toAuthResponse(user);
    }

    private AuthResponseDto toAuthResponse(User user) {
        // For this foundation milestone, userId is used as an API token in X-User-Id.
        return new AuthResponseDto(user.getId(), user.getEmail(), user.getName(), user.isAdmin(), user.getId().toString());
    }

    private boolean isPasswordValid(User user, String rawPassword) {
        // Seed data ships a placeholder hash; keep a deterministic bootstrap password for local admin login.
        if ("$2a$10$hash".equals(user.getPasswordHash())) {
            return "password".equals(rawPassword);
        }
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}

