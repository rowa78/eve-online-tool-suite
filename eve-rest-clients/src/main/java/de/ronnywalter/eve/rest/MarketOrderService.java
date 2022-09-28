package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.MarketGroupDTO;
import de.ronnywalter.eve.dto.MarketOrderDTO;
import de.ronnywalter.eve.dto.TypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@FeignClient(name = "eve-backend/market", decode404 = true)
public interface MarketOrderService {

    @RequestMapping(method = RequestMethod.GET, value = "/{regionId}/orders")
    List<MarketOrderDTO> getMarketOrdersForRegion(@PathVariable("regionId") int regionId);

    @RequestMapping(method = RequestMethod.GET, value = "/order/{orderId}")
    MarketOrderDTO getMarketOrder(@PathVariable("orderId") int orderId);

    @PutMapping(value="/order/{orderId}")
    MarketOrderDTO saveOrder(@PathVariable("orderId") long orderId, @RequestBody MarketOrderDTO marketOrderDTO);

    @PostMapping(value="/order")
    MarketOrderDTO createOrder(@RequestBody MarketOrderDTO marketOrderDTO);

    @PostMapping(value="/orders")
    void createOrders(@RequestBody List<MarketOrderDTO> marketOrderDTOs);

    @DeleteMapping("/order/{orderId}")
    void deleteMarketOrder (@PathVariable("orderId") long orderId);

    @DeleteMapping("/orders}")
    void deleteMarketOrders (@RequestBody List<MarketOrderDTO> marketOrders);

    @GetMapping("/{regionId}/orderids")
    public List<Integer> getMarketOrderIdsForRegion (@PathVariable("regionId") int regionId);

}
