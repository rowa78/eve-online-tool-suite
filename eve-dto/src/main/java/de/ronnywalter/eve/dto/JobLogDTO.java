package de.ronnywalter.eve.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class JobLogDTO {

    private String id;

    private String name;
    private long runCount;

    private String image;
    private List<String> arguments;

    private Instant startDate;
    private Instant endDate;

    private int exitCode;

    private String log;
}
