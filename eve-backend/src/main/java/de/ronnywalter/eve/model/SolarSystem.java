package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode (callSuper = true )
@Entity
@Table(name = "eve_systems")
public class SolarSystem extends DBEntity {

    @Id
    private Integer id;
    private String name;

    private double securityStatus;

    private int constellationId;

    private int regionId;

}
