package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true )
@Table(name = "eve_type_groups")
public class TypeGroup extends DBEntity {

    @Id
    private Integer id;
    private String name;

    private int categoryId;

}
