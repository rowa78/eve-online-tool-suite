package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.CreateEveCharacterDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.exception.EveCharacterNotFoundException;
import de.ronnywalter.eve.model.EveCharacter;
import de.ronnywalter.eve.service.CharacterService;
import de.ronnywalter.eve.service.JobService;
import de.ronnywalter.eve.service.UniverseService;
import de.ronnywalter.eve.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("chars")
@RequiredArgsConstructor
public class CharacterController extends AbstractController {

    private final CharacterService characterService;
    private final UniverseService universeService;
    private final UserService userService;

    private final JobService jobService;

    //private final CharacterUpdateJob characterUpdateJob;

    @GetMapping(value = "/{id}")
    @ResponseBody
    public EveCharacterDTO getCharacter(@PathVariable int id) {
        try{
            EveCharacter character = characterService.getEveCharacter(id);
            if(character != null) {
                return map(character, EveCharacterDTO.class);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public void deleteCharacter(@PathVariable int id) {
        characterService.deleteCharacter(id);
    }

    @PutMapping(value = "{id}")
    @ResponseBody
    public EveCharacterDTO updateCharacter(@PathVariable int id, @RequestBody EveCharacterDTO eveCharacterDTO) {
        if(characterService.characterExists(id)) {
            EveCharacter eveCharacter = map(eveCharacterDTO, EveCharacter.class);
            eveCharacter.setUser(userService.getUser(eveCharacterDTO.getUserId()));
            characterService.saveCharacter(eveCharacter);
            return map(eveCharacter, EveCharacterDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "")
    @ResponseBody
    public EveCharacterDTO createCharacter(@RequestBody EveCharacterDTO eveCharacterDTO) {
        boolean characterExists = characterService.characterExists(eveCharacterDTO.getId());

        if(characterExists) {
            EveCharacter existingCharacter = characterService.getEveCharacter(eveCharacterDTO.getId());
            existingCharacter.setUser(userService.getUser(eveCharacterDTO.getUserId()));
            characterService.saveCharacter(existingCharacter);
            return map(existingCharacter, EveCharacterDTO.class);
        } else {
            log.info("Storing character: " + eveCharacterDTO.getId());
            EveCharacter character = map(eveCharacterDTO, EveCharacter.class);
            character.setUser(userService.getUser(eveCharacterDTO.getUserId()));
            characterService.saveCharacter(character);
            jobService.rescheduleCharacterBasedTasks();

            return map(character, EveCharacterDTO.class);
        }
    }


    @GetMapping(value = "")
    @ResponseBody
    public List<EveCharacterDTO> getCharacters() {
        List<EveCharacter> result = characterService.getEveCharacters();
        return mapList(result, EveCharacterDTO.class);
    }

}
