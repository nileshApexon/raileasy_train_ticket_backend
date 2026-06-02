package com.raileasy.common.security;

import java.security.Principal;
import java.util.UUID;

public class JwtUserPrincipal implements Principal {

    private final UUID userId;
    private final String email;
    private final boolean isAdmin;

    public JwtUserPrincipal(UUID userId, String email, boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getName() {
        return email;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
