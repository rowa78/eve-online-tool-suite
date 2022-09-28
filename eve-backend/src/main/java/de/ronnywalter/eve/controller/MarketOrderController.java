package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.MarketDataDTO;
import de.ronnywalter.eve.dto.MarketOrderDTO;
import de.ronnywalter.eve.exception.EntityNotFoundException;
import de.ronnywalter.eve.model.MarketData;
import de.ronnywalter.eve.model.MarketOrder;
import de.ronnywalter.eve.service.MarketDataService;
import de.ronnywalter.eve.service.MarketOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping ("market")
@RequiredArgsConstructor
@Slf4j
public class MarketOrderController extends AbstractController {

    private final MarketOrderService marketOrderService;

    @GetMapping("/order/{orderId}")
    public MarketOrderDTO getMarketOrder (@PathVariable long orderId) {
        MarketOrder order = marketOrderService.getMarketOrder(orderId);
        return map(order, MarketOrderDTO.class);
    }

    @DeleteMapping("/order/{orderId}")
    public void deleteMarketOrder (@PathVariable long orderId) {
        try {
            marketOrderService.deleteMarketOrder(orderId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/orders}")
    public void deleteMarketOrders (@RequestBody List<MarketOrder> marketOrders) {
        List<MarketOrder> orders = mapList(marketOrders, MarketOrder.class);
        marketOrderService.deleteMarketOrders(orders);
    }

    @PostMapping("/order")
    public MarketOrderDTO createMarketOrder (@RequestBody MarketOrderDTO marketOrderDTO) {
        MarketOrder order = map(marketOrderDTO, MarketOrder.class);
        return map(marketOrderService.saveMarketOrder(order), MarketOrderDTO.class);
    }

    @PostMapping("/orders")
    public void createMarketOrders (@RequestBody List<MarketOrderDTO> marketOrderDTOs) {
        List<MarketOrder> orders = mapList(marketOrderDTOs, MarketOrder.class);
        marketOrderService.saveMarketOrders(orders);
    }

    @PutMapping("/order/{orderId}")
    public MarketOrderDTO createMarketOrder (@PathVariable long orderId, @RequestBody MarketOrderDTO marketOrderDTO) {
        MarketOrder order = map(marketOrderDTO, MarketOrder.class);
        if(order.getOrderId() == orderId) {
            return map(marketOrderService.saveMarketOrder(order), MarketOrderDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "given orderid does not match to id in body");
        }
    }

    @GetMapping("/{regionId}/orders")
    public List<MarketOrderDTO> getMarketOrdersForRegion (@PathVariable int regionId) {
        List<MarketOrder> orders = marketOrderService.getMarketOrdersForRegion(regionId);
        return mapList(orders, MarketOrderDTO.class);
    }

    @GetMapping("/{regionId}/orderids")
    public List<Integer> getMarketOrderIdsForRegion (@PathVariable int regionId) {
        List<Integer> orders = marketOrderService.getMarketOrderIdsForRegion(regionId);
        return orders!=null?orders:new ArrayList<>();
    }

}
