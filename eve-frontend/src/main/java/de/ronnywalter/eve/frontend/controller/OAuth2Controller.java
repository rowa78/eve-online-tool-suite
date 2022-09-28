package de.ronnywalter.eve.frontend.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import de.ronnywalter.eve.dto.ApiTokenDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.UserDTO;
import de.ronnywalter.eve.frontend.config.EveEsiProperties;
import de.ronnywalter.eve.frontend.security.*;
import de.ronnywalter.eve.frontend.service.SecurityService;
import de.ronnywalter.eve.rest.CharacterService;
import de.ronnywalter.eve.rest.TokenService;
import de.ronnywalter.eve.rest.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private static final String KEY_USER = "user";
    private static final String KEY_STATE = "state";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_SERVICE_ID = "serviceId";

    private final OAuth20Service oAuthService;
    private final EveEsiProperties eveEsiProperties;

    private final SecurityService securityService;
    private final UserService userService;
    private final TokenService tokenService;
    private final CharacterService characterService;

    private String state;

    @GetMapping("/eve-esi/authorization/eve-characters")
    public RedirectView oauth2Login() {
        this.state = UUID.randomUUID().toString();
        return new RedirectView(oAuthService.getAuthorizationUrl(state));
    }

    @GetMapping("/eve-esi/code/eve-characters")
    public RedirectView oauth2Code(String code, String state) throws InterruptedException, ExecutionException, IOException {
        if (!Objects.equals(state, this.state)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "EveCharacter Not Found");
        } else {
            OAuth2AccessToken accessToken = oAuthService.getAccessToken(code);
            OAuthRequest request = new OAuthRequest(Verb.GET, eveEsiProperties.getVerifyUrl());
            oAuthService.signRequest(accessToken, request);

            Response response = oAuthService.execute(request);
            System.out.println(accessToken.getAccessToken());
            System.out.println(accessToken.getRefreshToken());

            System.out.println(response.getBody());
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> values =  springParser.parseMap(response.getBody());
            Integer id = Integer.valueOf(values.get("CharacterID").toString());
            String name = values.get("CharacterName").toString();



            if(!securityService.isUserLoggedIn()) {
                EveUserDetails user = new EveUserDetails();
                user.setId(id);
                user.setName(name);
                user.setAccessToken(accessToken.getAccessToken());
                user.setRefreshToken(accessToken.getRefreshToken());
                user.setExpiresIn(accessToken.getExpiresIn());
                securityService.setAuthenticatedUser(user);

                de.ronnywalter.eve.dto.UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setName(user.getName());
                userDTO.setLastLogin(Instant.now());
                userService.createUser(userDTO);

                EveCharacterDTO eveCharacterDTO = new EveCharacterDTO();
                eveCharacterDTO.setId(user.getId());
                eveCharacterDTO.setName(user.getName());
                eveCharacterDTO.setUserId(securityService.getAuthenticatedUser().getId());
                characterService.createCharacter(eveCharacterDTO);
            } else {
                EveCharacterDTO eveCharacterDTO = new EveCharacterDTO();
                eveCharacterDTO.setId(id);
                eveCharacterDTO.setName(name);
                eveCharacterDTO.setUserId(securityService.getAuthenticatedUser().getId());
                characterService.createCharacter(eveCharacterDTO);
            }
            ApiTokenDTO apiTokenDTO = ApiTokenDTO.builder()
                    .accessToken(accessToken.getAccessToken())
                    .refreshToken(accessToken.getRefreshToken())
                    .expiryDate(LocalDateTime.now().plusSeconds(accessToken.getExpiresIn()).atZone(ZoneId.systemDefault()))
                    .clientId(eveEsiProperties.getClientId())
                    .clientSecret(eveEsiProperties.getClientSecret())
                    .characterId(id)
                    .build();

            tokenService.createApiToken(apiTokenDTO);

            /*EveCharacter eveCharacter = characterService.getEveCharacter(id);
            if(eveCharacter == null) {
                eveCharacter = new EveCharacter();
                eveCharacter.setId(id);
            }
            eveCharacter.setName(name);
            User user = userService.getUser(authenticatedUser.getAuthenticatedUser().get().getId());
            eveCharacter.setUser(user);
            eveCharacter.setApiToken(accessToken.getAccessToken());
            eveCharacter.setRefreshToken(accessToken.getRefreshToken());
            eveCharacter.setExpiryDate(LocalDateTime.now().plusSeconds(accessToken.getExpiresIn()));

            characterService.saveCharacter(eveCharacter);
            characterUpdateJob.updateChar(id);


             */
            return new RedirectView("/");
        }
    }
}
