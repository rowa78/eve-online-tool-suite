package de.ronnywalter.eve.frontend.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import de.ronnywalter.eve.dto.ApiTokenDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.UserDTO;
import de.ronnywalter.eve.frontend.config.EveEsiProperties;
import de.ronnywalter.eve.frontend.security.EveOnlineAuthentication;
import de.ronnywalter.eve.frontend.security.EveUserDetails;
import de.ronnywalter.eve.frontend.security.UserRole;
import de.ronnywalter.eve.rest.TokenService;
import de.ronnywalter.eve.rest.CharacterService;
import de.ronnywalter.eve.rest.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityService {


    private final UserService userService;
    private final CharacterService characterService;
    private final TokenService tokenService;
    private final EveEsiProperties eveEsiProperties;

    private static final String LOGOUT_SUCCESS_URL = "/";

    public boolean isUserLoggedIn() {
        EveUserDetails userDetails = getAuthenticatedUser();
        if(userDetails != null) {
            return true;
        } else {
            return false;
        }
    }

    public EveUserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication != null && authentication instanceof EveOnlineAuthentication) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof EveUserDetails) {
                return (EveUserDetails) context.getAuthentication().getPrincipal();
            }
        }
        // Anonymous or no authentication.
        return null;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }

    public void setAuthenticatedUser(EveUserDetails user) {
        EveOnlineAuthentication auth = new EveOnlineAuthentication();
        auth.setPrincipal(user);
        auth.setAuthenticated(true);
        auth.addAuthority(new UserRole());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }
}