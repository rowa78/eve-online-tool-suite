package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.MarketGroupDTO;
import de.ronnywalter.eve.dto.TypeDTO;
import de.ronnywalter.eve.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/marketgroups", decode404 = true)
public interface MarketGroupService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<MarketGroupDTO> getMarketGroups();

    @RequestMapping(method = RequestMethod.GET, value = "/{marketGroupId}")
    MarketGroupDTO getMarketGroup(@PathVariable("marketGroupId") int marketGroupId);

    @RequestMapping(method = RequestMethod.GET, value = "/{marketGroupId}/types")
    List<TypeDTO> getTypesOfMarketGroup(@PathVariable("marketGroupId") int marketGroupId);

    @PostMapping(value="")
    MarketGroupDTO createMarketGroup(@RequestBody MarketGroupDTO marketGroupDTO);

}
