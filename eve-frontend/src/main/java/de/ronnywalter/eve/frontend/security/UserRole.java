package de.ronnywalter.eve.frontend.security;

import org.springframework.security.core.GrantedAuthority;

public class UserRole implements GrantedAuthority {
    @Override
    public String getAuthority() {
        return "ROLE_USER";
    }
}
