package de.ronnywalter.eve.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {

    public static final String STATION="station";
    public static final String STRUCTURE="structure";

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
