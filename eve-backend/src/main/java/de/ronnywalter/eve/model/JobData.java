package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "eve_job_data")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobData extends DBEntity {

    @Id
    private String name;
}
