package de.ronnywalter.eve.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JobDefinition implements Serializable {

    static final long serialVersionUID = 1L;

    private String name;
    private String jobName;
    private long runCount;
    private List<String> arguments;
}
