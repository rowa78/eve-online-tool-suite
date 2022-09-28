package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.CharacterLocationDTO;
import de.ronnywalter.eve.dto.CharacterWalletDTO;
import de.ronnywalter.eve.exception.EveCharacterNotFoundException;
import de.ronnywalter.eve.model.CharacterWallet;
import de.ronnywalter.eve.model.EveCharacter;
import de.ronnywalter.eve.model.Location;
import de.ronnywalter.eve.service.CharacterService;
import de.ronnywalter.eve.service.UniverseService;
import de.ronnywalter.eve.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("chars")
@Slf4j
public class LocationController extends AbstractController {

    private final UniverseService universeService;
    private final CharacterService characterService;

    @GetMapping(value = "/{id}/location")
    @ResponseBody
    public CharacterLocationDTO getCharacterLocation(@PathVariable int id) {
        try{
            EveCharacter character = characterService.getEveCharacter(id);
            if(character != null) {
                CharacterLocationDTO characterLocationDTO = new CharacterLocationDTO();
                characterLocationDTO.setId(id);
                characterLocationDTO.setLocationId(character.getLocationId());
                return characterLocationDTO;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/{id}/location")
    @ResponseBody
    public CharacterLocationDTO createCharacterWallet(@PathVariable int id, @RequestBody CharacterLocationDTO characterLocationDTO) {
        EveCharacter character = characterService.getEveCharacter(id);
        if(character != null) {
            character.setLocationId(characterLocationDTO.getLocationId());
            Location loc = universeService.getLocation(character.getLocationId());
            if(loc != null) {
                character.setLocationName(loc.getName());
                character.setSolarSystemId(loc.getSolarsystemId());
                character.setSolarSystemName(universeService.getSolarSystem(loc.getSolarsystemId()).getName());
            }
            characterService.saveCharacter(character);
            return characterLocationDTO;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
