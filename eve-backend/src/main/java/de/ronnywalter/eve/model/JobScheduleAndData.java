package de.ronnywalter.eve.model;

import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.helper.ScheduleAndData;
import com.github.kagkarlsson.scheduler.task.schedule.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class JobScheduleAndData implements ScheduleAndData, Serializable {

    private Instant nextExecutionTime;
    private JobDefinition jobDefinition;

    public Schedule getSchedule() {
        return new Schedule() {
            @Override
            public Instant getNextExecutionTime(ExecutionComplete executionComplete) {
                return nextExecutionTime;
            }

            @Override
            public boolean isDeterministic() {
                return true;
            }
        };
    }

    @Override
    public JobDefinition getData() {
        return jobDefinition;
    }
}
