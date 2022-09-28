package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.ApiTokenDTO;
import de.ronnywalter.eve.exception.TokenNotFoundException;
import de.ronnywalter.eve.model.Token;
import de.ronnywalter.eve.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping(value = "/{characterId}")
    @ResponseBody
    public String getAccessToken(@PathVariable int characterId) {
        try{
            return tokenService.getAccessToken(characterId);
        } catch (TokenNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping(value = "")
    @ResponseBody
    public ApiTokenDTO createToken(@RequestBody ApiTokenDTO apiTokenDTO) {
        Token token = tokenService.getToken(apiTokenDTO.getCharacterId());
        if(token == null) {
            token = new Token();
            token.setCharacterId(apiTokenDTO.getCharacterId());
        }
        token.setAccessToken(apiTokenDTO.getAccessToken());
        token.setRefreshToken(apiTokenDTO.getRefreshToken());
        token.setClientId(apiTokenDTO.getClientId());
        token.setClientSecret(apiTokenDTO.getClientSecret());
        token.setExpiryDate(apiTokenDTO.getExpiryDate().toLocalDateTime());

        tokenService.saveToken(token);
        return apiTokenDTO;
    }
}
