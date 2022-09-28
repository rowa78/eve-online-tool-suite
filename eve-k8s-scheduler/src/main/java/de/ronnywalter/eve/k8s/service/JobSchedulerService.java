package de.ronnywalter.eve.k8s.service;

import com.github.kagkarlsson.scheduler.ScheduledExecution;
import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.SchedulableInstance;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import de.ronnywalter.eve.k8s.JobDefinition;
import de.ronnywalter.eve.k8s.JobDefinitionDTO;
import de.ronnywalter.eve.k8s.KubernetesJob;
import de.ronnywalter.eve.k8s.model.JobScheduleAndData;
import de.ronnywalter.eve.k8s.repository.JobConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobSchedulerService {

    private final Scheduler scheduler;
    private final KubernetesJob kubernetesJob;

    @Value("${scheduler.queues.jobs.schedule}")
    public String queueName;

    @RabbitListener (queues = "${scheduler.queues.jobs.schedule}")
    public void scheduleJobOnK8s(JobDefinitionDTO job) {
        log.info("got job: " + job);

        List<ScheduledExecution<Object>> scheduledExecutionList = scheduler.getScheduledExecutions();

        scheduledExecutionList.forEach(scheduledExecution -> {
            JobDefinitionDTO d = (JobDefinitionDTO)scheduledExecution.getData();
            log.info("Scheduled Execution: " + d.getName() + ", " + scheduledExecution.getExecutionTime() + ", picked: " + scheduledExecution.getPickedBy());
        });

        scheduler.getCurrentlyExecuting().forEach(c -> {
            log.info(c.getTaskInstance().getId() + ", " + c.getTaskInstance().getTaskName() + ", " + c.getExecution().getExecutionTime());
        });

        //scheduler.schedule(kubernetesJob.instance(job.getName() + "-" + UUID.randomUUID().toString(), job), job.getScheduleTime());

        ScheduledExecution<Object> scheduledExecution = getSchedulesExecution(job.getName());
        if(scheduledExecution == null) {
            scheduler.schedule(kubernetesJob.instance(job.getName() + "-" + UUID.randomUUID().toString(), job), job.getScheduleTime());
        } else {
            log.info("Job " + job.getName() + " is already scheduled.");
            if(job.isForceReschedule()) {
                log.info("forcing reschedule of job " + job.getName());
                scheduler.cancel(scheduledExecution.getTaskInstance());
                scheduler.schedule(kubernetesJob.instance(job.getName() + "-" + UUID.randomUUID().toString(), job), job.getScheduleTime());
            }
        }
    }

    private ScheduledExecution<Object> getSchedulesExecution(String name) {
        List<ScheduledExecution<Object>> scheduledExecutionList = scheduler.getScheduledExecutions();

        for (ScheduledExecution<Object> scheduledExecution: scheduledExecutionList) {
            JobDefinitionDTO d = (JobDefinitionDTO)scheduledExecution.getData();
            if(d.getName().equals(name)) {
                return scheduledExecution;
            }
        }
        return null;
    }
}
