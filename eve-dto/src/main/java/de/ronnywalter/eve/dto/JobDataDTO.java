package de.ronnywalter.eve.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class JobDataDTO {

    private String name;
    private Instant nextExecutionTime;
    private String eTag;

}
