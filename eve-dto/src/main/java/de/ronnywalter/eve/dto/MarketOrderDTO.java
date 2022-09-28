package de.ronnywalter.eve.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MarketOrderDTO {

    private long orderId;

    private int duration;

    private boolean isBuyOrder;
    private LocalDateTime issuedDate;

    private int minVolume;
    private double price;
    private int volumeTotal;
    private int volumeRemain;

    private String orderRange;

    private int regionId;

    private int typeId;

    private long locationId;
}
