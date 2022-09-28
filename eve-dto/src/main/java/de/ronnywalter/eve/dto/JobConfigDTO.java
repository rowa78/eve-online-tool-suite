package de.ronnywalter.eve.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class JobConfigDTO {

    private String name;
    private String image;
    private String version;
    private Instant buildTime;

    private boolean characterBased;

}
