package de.ronnywalter.eve.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "eve_job_log")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobLog extends DBEntity {

    @Id
    private String id;

    private String name;
    private long runCount;

    private String image;
    @ElementCollection
    @CollectionTable(name="eve_job_log_arguments")
    private List<String> arguments;

    private Instant startDate;
    private Instant endDate;

    private int exitCode;

    @Column(columnDefinition="TEXT")
    private String log;

}
