package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.CharacterLocationDTO;
import de.ronnywalter.eve.dto.CharacterWalletDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/chars", decode404 = true)
public interface CharacterService {

    @RequestMapping(method = RequestMethod.GET, value = "/{characterId}")
    EveCharacterDTO getCharacter(@PathVariable("characterId") int characterId);

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<EveCharacterDTO> getCharacters();

    @PostMapping(value="")
    EveCharacterDTO createCharacter(@RequestBody EveCharacterDTO eveCharacterDTO);

    @PutMapping(value="{id}")
    EveCharacterDTO updateCharacter(@PathVariable("id") int id, @RequestBody EveCharacterDTO eveCharacterDTO);

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/wallet")
    CharacterWalletDTO getWallet(@PathVariable("id") int id);

    @PutMapping(value = "/{id}/wallet")
    CharacterWalletDTO updateWallet(@PathVariable("id") int id, @RequestBody CharacterWalletDTO locationDTO);

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/location")
    CharacterLocationDTO getLocation(@PathVariable("id") int id);

    @PutMapping(value = "/{id}/location")
    CharacterLocationDTO updateLocation(@PathVariable("id") int id, @RequestBody CharacterLocationDTO locationDTO);
}
