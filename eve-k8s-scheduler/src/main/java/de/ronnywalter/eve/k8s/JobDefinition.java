package de.ronnywalter.eve.k8s;

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
