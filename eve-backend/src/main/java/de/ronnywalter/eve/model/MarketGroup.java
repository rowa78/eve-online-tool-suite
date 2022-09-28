package de.ronnywalter.eve.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "eve_market_groups")
public class MarketGroup extends DBEntity {

    @Id
    private int id;
    @Column(columnDefinition="TEXT")
    private String description;
    private String name;

    private Integer parentId;

    @Transient
    private List<MarketGroup> children;

    private boolean hasTypes;
    private Integer iconId;
    private String icon;
}
