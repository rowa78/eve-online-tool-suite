package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.MarketDataDTO;
import de.ronnywalter.eve.model.MarketData;
import de.ronnywalter.eve.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (name = "marketdata")
@RequiredArgsConstructor
@Slf4j
public class MarketDataController extends AbstractController {

    private final MarketDataService marketDataService;

    @GetMapping("marketdata/{regionId}/{typeId}")
    public MarketDataDTO getMarketData (@PathVariable int regionId, @PathVariable int typeId) {
        MarketData marketData = marketDataService.getMarketData(typeId, regionId);
        MarketDataDTO dto = new MarketDataDTO();

        return map(marketData, MarketDataDTO.class);
    }


}
