package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.MarketGroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient (name = "eve-sde/marketgroups", decode404 = true)
public interface MarketGroupSDEService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<MarketGroupDTO> getMarketGroups();

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    MarketGroupDTO getMarketGroup(@PathVariable("id") int id);
}
