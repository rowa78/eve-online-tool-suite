package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.CorpDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.UserDTO;
import de.ronnywalter.eve.exception.EveCharacterNotFoundException;
import de.ronnywalter.eve.model.User;
import de.ronnywalter.eve.service.CharacterService;
import de.ronnywalter.eve.service.CorporationService;
import de.ronnywalter.eve.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController extends AbstractController {

    private final UserService userService;
    private final CharacterService characterService;
    private final CorporationService corporationService;

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public UserDTO getUser(@PathVariable int userId) {
        try{
            User user = userService.getUser(userId);
            if(user != null) {
                return createDto(user);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/{userId}/chars")
    @ResponseBody
    public List<EveCharacterDTO> getCharacters(@PathVariable int userId) {
        try{
            User user = userService.getUser(userId);
            if(user != null) {
                return mapList(characterService.getEveCharactersForUser(userId), EveCharacterDTO.class);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/{userId}/corporations")
    @ResponseBody
    public List<CorpDTO> getCorps(@PathVariable int userId) {
        try{
            User user = userService.getUser(userId);
            if(user != null) {
                return mapList(corporationService.getCorporationsForUser(userId), CorpDTO.class);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseBody
    public void deleteUser(@PathVariable int userId) {
        if(userService.getUser(userId) != null) {
            userService.deleteUser(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "")
    @ResponseBody
    public UserDTO createUser(@RequestBody UserDTO userDTO) {

        User user = userService.getUser(userDTO.getId());
        if(user != null) {
            user.setName(userDTO.getName());
            user.setLastLogin(Instant.now());
        } else {
            user = new User();
            user.setId(userDTO.getId());
            user.setName(userDTO.getName());
            user.setLastLogin(userDTO.getLastLogin());
        }

        return map(userService.saveUser(user), UserDTO.class);

    }


    @GetMapping(value = "")
    @ResponseBody
    public List<UserDTO> getUsers() {
        List<UserDTO> result = new ArrayList<>();
        return mapList(userService.getUsers(), UserDTO.class);
    }

    private UserDTO createDto(User user) {
        UserDTO dto = map(user, UserDTO.class);
        return dto;
    }

}
