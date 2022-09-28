package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.CorpDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.MarketGroupDTO;
import de.ronnywalter.eve.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/user", decode404 = true)
public interface UserService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<UserDTO> getUsers();

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    UserDTO getUser(@PathVariable("userId") int userId);

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/chars")
    List<EveCharacterDTO> getCharachtersForUser(@PathVariable("userId") int userId);

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/corporations")
    List<CorpDTO> getCorporationsForUser(@PathVariable("userId") int userId);

    @PostMapping(value = "")
    UserDTO createUser(@RequestBody UserDTO userDTO);

}
