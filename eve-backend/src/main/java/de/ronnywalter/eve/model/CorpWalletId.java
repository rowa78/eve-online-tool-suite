package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class CorpWalletId implements Serializable {

    private int corpId;
    private int division;

}
