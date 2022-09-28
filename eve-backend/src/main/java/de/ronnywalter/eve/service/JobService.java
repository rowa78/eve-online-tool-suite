package de.ronnywalter.eve.service;
import com.github.kagkarlsson.scheduler.ScheduledExecution;
import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.SchedulableInstance;
import com.github.kagkarlsson.scheduler.task.TaskInstanceId;
import de.ronnywalter.eve.model.JobConfig;
import de.ronnywalter.eve.model.JobDefinition;
import de.ronnywalter.eve.model.JobScheduleAndData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final Scheduler scheduler = null;

    private final JobRegistryService jobRegistryService;

    //private KubernetesJob job;

    public void scheduleTask(JobDefinition jobDefinition) {
        scheduleTask(jobDefinition, false);
    }

    public void scheduleTask(JobDefinition jobDefinition, boolean triggerNow) {
        /*JobScheduleAndData jobScheduleAndData = new JobScheduleAndData();
        jobScheduleAndData.setJobDefinition(jobDefinition);
        jobScheduleAndData.setNextExecutionTime(Instant.now());
        String taskName = jobDefinition.getName().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
        SchedulableInstance<JobScheduleAndData> schedulableInstance = job.schedulableInstance(jobDefinition.getName(), jobScheduleAndData);

        TaskInstanceId id =  new TaskInstanceId.StandardTaskInstanceId("KubernetesJob", taskName);
        if (scheduler.getScheduledExecution(id).isPresent()) {
            ScheduledExecution<Object> scheduledExecution = scheduler.getScheduledExecution(id).get();
            JobScheduleAndData scheduledData = (JobScheduleAndData) scheduledExecution.getData();
            log.info("job " + id.getId() + " is already scheduled for " + scheduledData.getNextExecutionTime());
            if(triggerNow) {
                log.info("Triggering job, scheduling it to now");
                scheduledData.setNextExecutionTime(Instant.now());
                schedulableInstance = job.schedulableInstance(taskName, jobScheduleAndData);
                scheduler.reschedule(schedulableInstance);
            }
        } else {
            log.info("Scheduling new Task: " + taskName + " for " + jobScheduleAndData.getNextExecutionTime());
            scheduler.schedule(schedulableInstance);
        }

         */
    }

    public void rescheduleCharacterBasedTasks() {
        log.info("Rescheduling char-based tasks.");
        List<JobConfig> jobConfigs = jobRegistryService.getCharacterBasedJobConfigs();
        jobConfigs.forEach(j -> {
            String taskName = j.getName().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
            log.info("Reschedule init-job for: " + taskName);
            JobDefinition jobDefinition = new JobDefinition();
            jobDefinition.setName(taskName);
            jobDefinition.setJobName(j.getName());
            List<String> arguments = new ArrayList<>();
            arguments.add("--mode=init");
            jobDefinition.setArguments(arguments);
            scheduleTask(jobDefinition, true);
        });
    }
}
