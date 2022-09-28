package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Data
@EqualsAndHashCode (callSuper = true)
@Table(name = "eve_character_wallets")
public class CharacterWallet extends DBEntity implements Serializable {

    @Id
    private int characterId;
    private Double value;
}
