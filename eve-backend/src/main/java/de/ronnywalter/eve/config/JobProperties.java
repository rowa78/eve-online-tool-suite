package de.ronnywalter.eve.config;

import de.ronnywalter.eve.model.JobConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "jobs")
@Data
public class JobProperties {

    private boolean enabled;
    private List<JobConfig> jobs;

}
