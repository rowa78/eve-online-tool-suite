package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/corps", decode404 = true)
public interface CorporationService {

    @RequestMapping(method = RequestMethod.GET, value = "/{corpId}")
    CorpDTO getCorporation(@PathVariable("corpId") int corpId);

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<CorpDTO> getCorporations();

    @PostMapping(value="")
    CorpDTO createCorporation(@RequestBody CorpDTO eveCharacterDTO);

    @PutMapping(value="{id}")
    CorpDTO updateCorporation(@PathVariable("id") int id, @RequestBody CorpDTO corpDTO);


    @RequestMapping(method = RequestMethod.GET, value = "/{id}/wallet/{division}")
    CorpWalletDTO getWallet(@PathVariable("id") int id, @PathVariable("division") int division);

    @PutMapping(value = "/{id}/wallet/{division}")
    CorpWalletDTO updateWallet(@PathVariable("id") int id, @PathVariable("division") int division, @RequestBody CorpWalletDTO dto);

    /*
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/location")
    CharacterLocationDTO getLocation(@PathVariable("id") int id);

    @PutMapping(value = "/{id}/location")
    CharacterLocationDTO updateLocation(@PathVariable("id") int id, @RequestBody CharacterLocationDTO locationDTO);

     */
}
