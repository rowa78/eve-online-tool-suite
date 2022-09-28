package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.CorpDTO;
import de.ronnywalter.eve.exception.EveCharacterNotFoundException;
import de.ronnywalter.eve.model.Corporation;
import de.ronnywalter.eve.service.CorporationService;
import de.ronnywalter.eve.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("corps")
@RequiredArgsConstructor
@Slf4j
public class CorporationController extends AbstractController {

    private final CorporationService corporationService;
    //private final CorpUpdateJob corpUpdateJob;
    private final UserService userService;


    @GetMapping("")
    public List<CorpDTO> getCorps () {
        return mapList(corporationService.getCorporations(), CorpDTO.class);
    }

    @PostMapping("")
    public CorpDTO save (@RequestBody CorpDTO corpDTO) {
        Corporation corporation = map(corpDTO, Corporation.class);
        corporation.setUser(userService.getUser(corpDTO.getUserId()));

        //corpUpdateJob.updateCorp(corpId);
        //jobSchedulerService.scheduleAllJobs();
        return map(corporationService.saveCorp(corporation), CorpDTO.class);
    }

    @PutMapping("/{id}")
    public CorpDTO update (@PathVariable int id, @RequestBody CorpDTO corpDTO) {
        Corporation corporation = map(corpDTO, Corporation.class);
        corporation.setUser(userService.getUser(corpDTO.getUserId()));

        //corpUpdateJob.updateCorp(corpId);
        //jobSchedulerService.scheduleAllJobs();
        return map(corporationService.saveCorp(corporation), CorpDTO.class);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public CorpDTO getCorp(@PathVariable int id) {
        Corporation corp = corporationService.getCorporation(id);
        if(corp != null) {
          return map(corp, CorpDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public void deleteCorp(@PathVariable int id) {
        corporationService.deleteCorp(id);
    }

}
