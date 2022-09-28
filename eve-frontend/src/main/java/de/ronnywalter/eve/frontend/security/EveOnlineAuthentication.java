package de.ronnywalter.eve.frontend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EveOnlineAuthentication implements Authentication {

    private boolean authenticated = false;
    private EveUserDetails userDetails;
    private Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    public void addAuthority(GrantedAuthority grantedAuthority) {
        this.grantedAuthorities.add(grantedAuthority);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public EveUserDetails getPrincipal() {
        return this.userDetails;
    }

    public void  setPrincipal(EveUserDetails principal) {
        this.userDetails = principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return null;
    }
}
