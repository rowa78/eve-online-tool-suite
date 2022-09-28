package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = true )
@ToString
@Table(name = "eve_assets")
public class Asset extends DBEntity {

    @Id
    private long id;

    private Integer corpId;

    private Integer characterId;

    private int typeId;

    private String locationFlag;
    private String locationType;
    private int quantity;

    private long locationId;
    private String locationName;
    private boolean singleton;
    private boolean blueprintCopy;
    private String name;
}
