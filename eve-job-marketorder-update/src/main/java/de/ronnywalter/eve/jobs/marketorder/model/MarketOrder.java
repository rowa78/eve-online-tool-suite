package de.ronnywalter.eve.jobs.marketorder.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class MarketOrder {

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

    public void updateValues(MarketOrder newOrder) {
        if(newOrder != null) {
            this.duration = newOrder.duration;
            this.isBuyOrder = newOrder.isBuyOrder;
            this.issuedDate = newOrder.issuedDate;
            this.minVolume = newOrder.minVolume;
            this.price = newOrder.price;
            this.volumeTotal = newOrder.volumeTotal;
            this.orderRange = newOrder.orderRange;
            this.typeId = newOrder.typeId;
            this.regionId = newOrder.regionId;
            this.locationId = newOrder.locationId;
        }
    }
}
