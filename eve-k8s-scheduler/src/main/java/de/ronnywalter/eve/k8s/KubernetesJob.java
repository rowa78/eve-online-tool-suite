package de.ronnywalter.eve.k8s;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule;
import de.ronnywalter.eve.dto.queues.Queues;
import de.ronnywalter.eve.k8s.model.JobConfig;
import de.ronnywalter.eve.k8s.model.JobScheduleAndData;
import de.ronnywalter.eve.k8s.service.JobRegistryService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class KubernetesJob extends OneTimeTask<JobDefinitionDTO> {

    @Autowired
    private KubernetesClient kubernetesClient;

    @Autowired
    private JobRegistryService jobRegistryService;

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private String rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUserName;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${scheduler.namespace}")
    private String namespace;

    @Value("${scheduler.serviceAccount}")
    private String serviceAccount;

    public KubernetesJob() {
        super("KubernetesJob", JobDefinitionDTO.class);
    }

    @Override
    public void executeOnce(TaskInstance<JobDefinitionDTO> taskInstance, ExecutionContext executionContext) {
        JobDefinitionDTO jobDefinition = taskInstance.getData();
        log.info("Executing: " + jobDefinition);

        JobConfig jobConfig = jobRegistryService.getJobConfig(jobDefinition.getJobName());

        if(jobConfig != null) {

            long runId = jobDefinition.getRunId();
            String taskName = jobDefinition.getName().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
            String k8sJobName = taskName + "-" + runId;

            List<String> args = new ArrayList<>(jobDefinition.getArguments());
            args.add("--taskName=" + taskName);
            args.add("--jobName=" + jobDefinition.getJobName());
            args.add("--image=" + jobConfig.getImage());
            args.add("--runId=" + runId);
            args.add("--spring.rabbitmq.host=" + rabbitHost);
            args.add("--spring.rabbitmq.port=" + rabbitPort);
            args.add("--spring.rabbitmq.username=" + rabbitUserName);
            args.add("--spring.rabbitmq.password=" + rabbitPassword);
            args.add("--scheduler.queues.jobs.schedule=" + Queues.EVE_JOBS_SCHEDULE);

            final Job job = new JobBuilder()
                    .withApiVersion("batch/v1")
                    .withNewMetadata()
                    .withName(k8sJobName)
                    .withLabels(Collections.singletonMap("label1", "maximum-length-of-63-characters"))
                    .withAnnotations(Collections.singletonMap("annotation1", "some-very-long-annotation"))
                    .endMetadata()
                    .withNewSpec()
                    .withTtlSecondsAfterFinished(5 * 60)
                    .withBackoffLimit(0)
                    .withNewTemplate()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("eve-job")
                    .withImage(jobConfig.getImage())
                    .withArgs(args.toArray(new String[0]))
                    .endContainer()
                    .withRestartPolicy("Never")
                    .withServiceAccount(serviceAccount)
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();


            log.info("Creating job " + k8sJobName);
            Job j = kubernetesClient.batch().v1().jobs().inNamespace(namespace).createOrReplace(job);


            // Get All pods created by the job
            PodList podList = kubernetesClient.pods().inNamespace(namespace).withLabel("job-name", k8sJobName).list();
            while (true) {
                log.debug("Waiting for pods of job " + k8sJobName);
                podList = kubernetesClient.pods().inNamespace(namespace).withLabel("job-name", k8sJobName).list();
                if (podList.getItems().size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            Pod pod = podList.getItems().get(0);

            while (true) {
                pod = kubernetesClient.pods().inNamespace(namespace).withName(pod.getMetadata().getName()).get();
                if (pod.getStatus().getPhase().equals("Succeeded") || pod.getStatus().getPhase().equals("Failed")) {
                    break;
                }
                log.debug("Status of pod: " + pod.getMetadata().getName() + " is " + pod.getStatus().getPhase());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info("Status of pod: " + pod.getMetadata().getName() + " is " + pod.getStatus().getPhase());
            // Print Job's log
            String joblog = kubernetesClient.batch().v1().jobs().inNamespace(namespace).withName(k8sJobName).getLog();
            //log.info(joblog);

            boolean suceeded = pod.getStatus().getPhase().equals("Succeeded");
            if (!suceeded) {
                log.error("job " + k8sJobName + " failed.");
                log.error(joblog);
                throw new RuntimeException("job " + k8sJobName + " failed.");
            } else {
                kubernetesClient.batch().v1().jobs().inNamespace(namespace).withName(k8sJobName).delete();
            }
        } else {
            throw new RuntimeException("no jobconfig for " + jobDefinition.getJobName() + " found.");
        }
    }
}
