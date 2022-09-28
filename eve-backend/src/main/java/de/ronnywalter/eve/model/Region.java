package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode (callSuper = true )
@Table(name = "eve_regions")
public class Region extends DBEntity {

    @Id
    private Integer id;
    private String name;


    @ToString.Exclude
    @Column(columnDefinition="TEXT")
    private String description;

}
