package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode (callSuper = true )
@Table(name = "eve_transactions")
public class Transaction extends DBEntity {

    @Id
    private long transactionId;

    private double unitPrice;
    private int quantity;

    private long clientId;
    private String clientName;
    private LocalDateTime date;

    private boolean buy;
    private boolean personal;

    private long journalRefId;

    private long locationId;

    private int typeId;
    private String type;

    private Integer characterId;
    private String characterName;
    private Integer corpId;
    private String corpName;
    private String corpTicker;
    private Integer division;

    public Double getSum() {
        return quantity * unitPrice;
    }
    public String getOrderType() {
        return isBuy() ? "buy" : "sell";
    }
}
