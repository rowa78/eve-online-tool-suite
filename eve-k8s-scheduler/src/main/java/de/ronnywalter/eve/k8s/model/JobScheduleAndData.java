package de.ronnywalter.eve.k8s.model;

import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.helper.ScheduleAndData;
import com.github.kagkarlsson.scheduler.task.schedule.Schedule;
import de.ronnywalter.eve.k8s.JobDefinitionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Slf4j
public class JobScheduleAndData implements ScheduleAndData, Serializable {

    private Instant nextExecutionTime;
    private JobDefinitionDTO jobDefinition;

    public Schedule getSchedule() {
        return new Schedule() {
            @Override
            public Instant getNextExecutionTime(ExecutionComplete executionComplete) {
                log.info("===============> " + nextExecutionTime);
            return nextExecutionTime;
            }

            @Override
            public boolean isDeterministic() {
                return true;
            }
        };
    }

    @Override
    public JobDefinitionDTO getData() {
        return jobDefinition;
    }
}
