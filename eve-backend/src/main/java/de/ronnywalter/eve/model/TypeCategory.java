package de.ronnywalter.eve.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (callSuper = true )
@Table(name = "eve_type_categories")
public class TypeCategory extends DBEntity {

    @Id
    private Integer id;
    private String name;

}
