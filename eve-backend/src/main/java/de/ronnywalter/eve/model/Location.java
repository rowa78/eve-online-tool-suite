package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode (callSuper = true)
@Table(name = "eve_locations")
public class Location extends DBEntity {

    public static final String STATION="station";
    public static final String STRUCTURE="structure";


    @Id
    private Long id;

    private String locationType;

    private String name;

    private Integer typeId;

    private int ownerCorpId;

    private int solarsystemId;
    private int constellationId;
    private int regionId;

    private Boolean accessForbidden;
    private Boolean hasMarket;

}
