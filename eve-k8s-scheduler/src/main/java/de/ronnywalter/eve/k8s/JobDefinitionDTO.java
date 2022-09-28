package de.ronnywalter.eve.k8s;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class JobDefinitionDTO implements Serializable {

    private String name;
    private String jobName;
    private long runId;
    private String namespace;
    private Instant scheduleTime;
    private List<String> arguments;
    private boolean forceReschedule;

}
