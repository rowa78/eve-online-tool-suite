package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.CharacterWalletDTO;
import de.ronnywalter.eve.dto.CorpWalletDTO;
import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.exception.EveCharacterNotFoundException;
import de.ronnywalter.eve.model.*;
import de.ronnywalter.eve.service.CharacterService;
import de.ronnywalter.eve.service.CorporationService;
import de.ronnywalter.eve.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Slf4j
public class WalletController extends AbstractController {

    private final WalletService walletService;
    private final CharacterService characterService;
    private final CorporationService corporationService;

    @GetMapping(value = "/chars/{id}/wallet")
    @ResponseBody
    public CharacterWalletDTO getCharacterWallet(@PathVariable int id) {
        try{
            CharacterWallet characterWallet = walletService.getCharacterWallet(id);
            if(characterWallet != null) {
                return map(characterWallet, CharacterWalletDTO.class);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/chars/{id}/wallet")
    @ResponseBody
    public CharacterWalletDTO createCharacterWallet(@PathVariable int id, @RequestBody CharacterWalletDTO characterWalletDTO) {
        try{
            EveCharacter eveCharacter = characterService.getEveCharacter(id);
            CharacterWallet wallet = map(characterWalletDTO, CharacterWallet.class);
            wallet.setCharacterId(eveCharacter.getId());
            wallet = walletService.saveCharacterWallet(wallet);
            return map(wallet, CharacterWalletDTO.class);
        } catch (EveCharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/corps/{id}/wallet/{division}")
    @ResponseBody
    public CorpWalletDTO getCorporationWallet(@PathVariable int id, @PathVariable int division) {
        CorpWallet corporationWallet = walletService.getCorpWallet(id, division);
        if(corporationWallet != null) {
            CorpWalletDTO dto = new CorpWalletDTO();
            dto.setCorpId(corporationWallet.getCorpId());
            dto.setDivision(corporationWallet.getDivision());
            dto.setValue(corporationWallet.getValue());
            return dto;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/corps/{id}/wallet/{division}")
    @ResponseBody
    public CorpWalletDTO updateCorpWallet(@PathVariable int id, @PathVariable int division, @RequestBody CorpWalletDTO dto) {
        CorpWallet corporationWallet = walletService.getCorpWallet(id, division);
        if(corporationWallet != null) {
            corporationWallet.setValue(dto.getValue());
            CorpWallet wallet = walletService.saveCorpWallet(corporationWallet);
            return dto;
        } else {
            CorpWallet c = new CorpWallet();
            c.setId(new CorpWalletId(id, division));
            c.setValue(dto.getValue());
            CorpWallet wallet = walletService.saveCorpWallet(c);
            return dto;
        }
    }

    @PostMapping(value = "/corps/{id}/wallet")
    @ResponseBody
    public CorpWalletDTO createCorporationWallet(@PathVariable int id, @RequestBody CorpWalletDTO corporationWalletDTO) {
        CorpWallet c = new CorpWallet();
        c.setId(new CorpWalletId(corporationWalletDTO.getCorpId(), corporationWalletDTO.getDivision()));
        c.setValue(corporationWalletDTO.getValue());
        CorpWallet wallet = walletService.saveCorpWallet(c);
        return corporationWalletDTO;
    }
}
