package de.ronnywalter.eve.dto;

import lombok.*;

@Getter
@Setter
public class SolarSystemDTO {

    private Integer id;
    private String name;

    private double securityStatus;

    private int constellationId;

    private int regionId;

}
