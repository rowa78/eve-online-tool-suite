package de.ronnywalter.eve.frontend.controller;

import de.ronnywalter.eve.dto.ApiTokenDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    @GetMapping("/loginCharacter")
    public RedirectView redirectWithUsingRedirectView(
            RedirectAttributes attributes) {
        return new RedirectView("oauth2/authorization/eve");
    }


    /**
     * Begin authentication process.  This will redirect to EVE sign on which will later redirect back
     * to /eveCallback.  The redirect URL you here should be the same as the one you entered for your
     * registered EVE application, and it should point to your callback request mapping.
     *
     * @return redirect URL to EVE sign on
     */
    @RequestMapping(value="/loginSuccess", method= RequestMethod.GET)
    public RedirectView loginSucess() {

        //System.out.println("login successful, token: " + authenticatedUser.getAuthenticatedUser().get().getName());

        /*
        UserDTO userDTO = authenticatedUser.getAuthenticatedUser().get();

        if(userDTO != null) {

            User user = userService.getUser(userDTO.getId());
            if(user == null) {
                user = new User();
                user.setId(userDTO.getId());
                user.setName(userDTO.getName());
            }
            user.setLastLogin(Instant.now());
            userService.saveUser(user);
        }
        */

        /*Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if(authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken =
                    (OAuth2AuthenticationToken) authentication;

            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            String name = attributes.get("CharacterName").toString();
            Integer id = Integer.valueOf(attributes.get("CharacterID").toString());

            OAuth2AuthorizedClient client =
                    clientService.loadAuthorizedClient(
                            oauthToken.getAuthorizedClientRegistrationId(),
                            oauthToken.getName());

            //client.getRefreshToken();

            /*String refreshToken = client.getRefreshToken().getTokenValue();
            String clientId = client.getClientRegistration().getClientId();

            EveCharacter eveCharacter = characterService.getEveCharacter(id);
           if(eveCharacter == null) {
               eveCharacter = new EveCharacter();
               eveCharacter.setId(id);
               eveCharacter.setName(name);
               characterService.saveCharacter(eveCharacter);
           }
            eveCharacter.setOwner(id);
            eveCharacter.setApiToken(client.getAccessToken().getTokenValue());
            eveCharacter.setRefreshToken(client.getRefreshToken().getTokenValue());

            DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            ZonedDateTime zdf = LocalDateTime.parse(attributes.get("ExpiresOn").toString(), dtf).atZone(ZoneId.of("UTC"));

            eveCharacter.setExpiryDate(zdf);
            eveCharacter.setClientId(clientId);
            characterService.saveCharacter(eveCharacter);
            jobSchedulerService.scheduleAllJobs();

         */


            //User user = new User();
            //user.setName(name);
            //user.setId(id);

            //userService.saveUser(user);
            //authentication.setAuthenticated(false);
       // }
        return new RedirectView("/");
    }

    @RequestMapping(value="/loginError", method= RequestMethod.GET)
    public String loginError() {
        return "Error!";
    }
}
