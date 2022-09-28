package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "eve_job_config")
@Entity
public class JobConfig extends DBEntity {

    @Id
    private String name;
    private String image;
    private String version;
    private Instant buildTime;

    private boolean characterBased;

}
