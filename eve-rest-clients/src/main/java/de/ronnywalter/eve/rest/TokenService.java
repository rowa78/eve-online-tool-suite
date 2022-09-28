package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.ApiTokenDTO;
import de.ronnywalter.eve.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/token", decode404 = true)
public interface TokenService {

    @RequestMapping(method = RequestMethod.GET, value = "/{characterId}")
    String getAccessToken(@PathVariable("characterId") int characterId);

    @PostMapping(value = "")
    ApiTokenDTO createApiToken(@RequestBody ApiTokenDTO apiTokenDTO);

}
