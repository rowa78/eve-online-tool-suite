package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@EqualsAndHashCode (callSuper = true)
@NoArgsConstructor
@Table(name = "eve_corp_wallets")
public class CorpWallet extends DBEntity {

    @EmbeddedId
    @Setter
    private CorpWalletId id;

    @Getter
    @Setter
    private Double value;

    public CorpWallet(int corp, int division) {
        this.id = new CorpWalletId(corp, division);
    }

    public int getCorpId() { return id.getCorpId(); }
    public int getDivision() { return id.getDivision(); }


}

