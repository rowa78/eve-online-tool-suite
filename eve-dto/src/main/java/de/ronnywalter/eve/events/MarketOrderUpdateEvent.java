package de.ronnywalter.eve.events;

import de.ronnywalter.eve.dto.MarketOrderDTO;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class MarketOrderUpdateEvent {

    private int regionId;

    private Instant date;

    private List<MarketOrderDTO> marketOrderDTOS;

}
