package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.model.*;
import de.ronnywalter.eve.service.MarketDataService;
import de.ronnywalter.eve.service.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("marketgroups")
@RequiredArgsConstructor
@Slf4j
public class MarketGroupController extends AbstractController {

    private final TypeService typeService;
    private final MarketDataService marketDataService;

    @GetMapping("")
    public List<MarketGroupDTO> getMarketGroups () {
        List<RegionDTO> regionDTOS = new ArrayList<>();
        List<MarketGroup> marketGroups = typeService.getRootMarketGroups(true);
        return mapList(marketGroups, MarketGroupDTO.class);
    }

    @GetMapping("/{id}")
    public MarketGroupDTO getMarketGroup (@PathVariable int id) {
        MarketGroup mg = typeService.getMarketGroup(id, true);
        return map(mg, MarketGroupDTO.class);
    }

    @GetMapping("/{id}/types")
    public List<TypeDTO> getTypesOfMarketGroup (@PathVariable int id) {
        List<Type> types = typeService.getTypesOfMarketGroup(id);
        return mapList(types, TypeDTO.class);
    }

    @PostMapping(value = "")
    public MarketGroupDTO saveMarketGroup(@RequestBody MarketGroupDTO marketGroupDTO) {
        return map(typeService.saveMarketGroup(map(marketGroupDTO, MarketGroup.class)), MarketGroupDTO.class);
    }
}
