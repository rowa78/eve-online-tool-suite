package de.ronnywalter.eve.jobs;


import com.google.common.collect.Lists;
import de.ronnywalter.eve.dto.JobConfigDTO;
import de.ronnywalter.eve.dto.JobDataDTO;
import de.ronnywalter.eve.dto.JobDefinitionDTO;
import de.ronnywalter.eve.dto.queues.Queues;
import de.ronnywalter.eve.rest.JobConfigService;
import de.ronnywalter.eve.rest.JobDataService;
import de.ronnywalter.eve.rest.JobService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.support.CronExpression;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public abstract class AbstractJob implements ApplicationRunner {

    public static final String JOB_NAME = "jobName";
    public static final String MODE = "mode";
    protected static final String REGISTER="register";
    protected static final String INIT="init";
    protected static final String WORKER="worker";
    public static final String IMAGE = "image";
    public static final String TASK_NAME = "taskName";
    public static final String ETAG = "etag";
    public static final String RUN_ID = "runId";

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private Environment environment;

    //@Autowired
    //protected JobDataService jobDataService;

    //@Autowired
    //protected JobService jobService;

    @Autowired
    protected JobConfigService jobConfigService;

    @Autowired
    protected BuildProperties buildProperties;

    @Autowired
    private ConfigurableApplicationContext ctx;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String taskName;
    private String jobName;
    private String image;
    private long runId;
    private String mode;

    private Boolean scheduleNextRun = true;
    private Instant nextScheduleTime;
    private Instant lastModified;

    private String etag;

    private JobDataDTO jobDataDTO;

    private ApplicationArguments applicationArguments;
    private boolean isCronJob = false;
    private String cronExpression = null;


    public AbstractJob() {


    };

    @PostConstruct
    private void init() {

    }

    //protected abstract void runRegistrationJob(ApplicationArguments args);
    protected abstract void runInitJob(ApplicationArguments args);
    protected abstract void runWorkerJob(ApplicationArguments args);
    protected abstract boolean isCharacterBasedJob();

    public void run(ApplicationArguments args) throws Exception {
        this.applicationArguments = args;
        args.getOptionNames().forEach(o -> {
            log.info("Option: " + o + "=" + String.join(", ", args.getOptionValues(o)));
        });

        this.jobName = getArgOptionValue(args, JOB_NAME);

        mode = getArgOptionValue(args, MODE);
        log.info("Mode: " + mode);
        switch (mode) {

            case REGISTER:
                register(args);
                break;
            case INIT:
                initializer(args);
                break;
            case WORKER:
                worker(args);
                break;
        }
/*


            if(args.containsOption("mode") && args.getOptionValues("mode").get(0).equals("register")) {

            } else if(args.containsOption("mode") && args.getOptionValues("mode").get(0).equals("init")) {

                if(args.containsOption("image")) {
                    this.image = args.getOptionValues("image").get(0);
                } else {
                    JobConfigDTO jobConfigDTO = jobConfigService.getJobConfig(taskName);
                    if(jobConfigDTO != null) {
                        this.image = jobConfigDTO.getImage();
                    }
                }

                runInitJob(args);

                Annotation annotation = AnnotationUtils.findAnnotation(this.getClass(), SchedulableJob.class);

                if(annotation != null) {
                    String scheduleTimeExpression = AnnotationUtils.getValue(annotation, "initScheduleTime").toString();
                    JobDataDTO jobDataDTO = new JobDataDTO();
                    jobDataDTO.setName(taskName);


                    if (scheduleNextRun) {
                        if (scheduleTimeExpression.length() > 0) {
                            CronExpression ce = CronExpression.parse(scheduleTimeExpression);
                            LocalDateTime next = ce.next(LocalDateTime.now());
                            jobDataDTO.setNextExecutionTime(next.toInstant(ZoneOffset.UTC));
                        } else {
                            jobDataDTO.setNextExecutionTime(getNextScheduleTime());
                        }
                    } else {
                        jobDataDTO.setNextExecutionTime(null);
                    }

                    log.info("Storing jobdata for " + taskName + ": " + jobDataDTO.toString());
                    jobDataService.saveJobData(taskName, jobDataDTO);
                }

            } else {
                log.info("Getting jobdata for task " + taskName);
                this.jobDataDTO = jobDataService.getJobData(taskName);
                log.info("got: " + jobDataDTO);
                if(this.jobDataDTO.getName() == null) {
                    this.jobDataDTO = new JobDataDTO();
                    this.jobDataDTO.setName(taskName);
                }

                runWorkerJob(args);

                Annotation annotation = AnnotationUtils.findAnnotation(this.getClass(), SchedulableJob.class);

                if(annotation != null) {
                    String scheduleTimeExpression = AnnotationUtils.getValue(annotation, "workerScheduleTime").toString();

                    if (scheduleNextRun) {
                        if (scheduleTimeExpression.length() > 0) {
                            CronExpression ce = CronExpression.parse(scheduleTimeExpression);
                            LocalDateTime next = ce.next(LocalDateTime.now());
                            jobDataDTO.setNextExecutionTime(next.toInstant(ZoneOffset.UTC));
                        } else {
                            jobDataDTO.setNextExecutionTime(getNextScheduleTime());
                        }
                    } else {
                        jobDataDTO.setNextExecutionTime(null);
                    }
                    jobDataDTO.setETag(getEtag());

                    log.info("Storing jobdata for " + taskName + ": " + jobDataDTO.toString());
                    jobDataService.saveJobData(taskName, jobDataDTO);


                    //rabbitTemplate.convertAndSend("eve-online-tool-suite", "eve.jobs.schedule", "Job: " + taskName + " finished.");
                }
            }
        //} else {
        //    log.error("Option mode is missing.", new RuntimeException("no mode set."));
        //}

 */
            log.info("Shutting down Application");
            ctx.close();
    }

    private void worker(ApplicationArguments args) {
        this.taskName = getArgOptionValue(args, TASK_NAME);
        this.image = getArgOptionValue(args, IMAGE);
        this.runId = Long.valueOf(getArgOptionValue(args, RUN_ID));
        if(args.containsOption(ETAG)) {
            this.etag = getArgOptionValue(args, ETAG);
        }
        runWorkerJob(args);
    }

    private void initializer(ApplicationArguments args) {
        this.taskName = getArgOptionValue(args, TASK_NAME);
        this.image = getArgOptionValue(args, IMAGE);
        this.runId = Long.valueOf(getArgOptionValue(args, RUN_ID));
        runInitJob(args);
    }

    private void register(ApplicationArguments args) {
        this.image = getArgOptionValue(args, IMAGE);
        //runRegistrationJob(args);
        storeJobConfig(isCharacterBasedJob());

        scheduleTask(getJobName() + "-init", INIT, new ArrayList<>(), Instant.now(), false);
        log.info("job registered and init-task event sent.");
    }

    private String getArgOptionValue(ApplicationArguments args, String name) {
        if(args.containsOption(name)) {
            return args.getOptionValues(name).get(0);

        } else {
            throw new RuntimeException("Option --" + name + " is not set.");
        }
    }

    protected void scheduleTask(String name, String mode, List<String> arguments, Instant scheduleTime) {
        scheduleTask(name, mode, arguments, scheduleTime, false);
    }

    protected void scheduleTask(String name, String mode, List<String> arguments, Instant scheduleTime, boolean forceSchedule) {
        long newRunId = runId + 1;
        //this.runCount = newRunCount;

        Set<String> args = new HashSet<>();
        args.add("--mode=" + mode);
        args.addAll(arguments);

        JobDefinitionDTO job = new JobDefinitionDTO();
        job.setJobName(getJobName());
        job.setName(name);
        job.setArguments(Lists.newArrayList(args));
        job.setScheduleTime(scheduleTime);
        job.setRunId(newRunId);
        job.setForceReschedule(forceSchedule);

        rabbitTemplate.convertAndSend(Queues.EVE_JOBS_SCHEDULE, job);
    }

    protected Instant getNextScheduleTime() {
        Annotation annotation = AnnotationUtils.findAnnotation(this.getClass(), SchedulableJob.class);

        if(annotation != null) {
            String scheduleTimeExpression = null;
            switch (mode) {
                case INIT:
                    scheduleTimeExpression = AnnotationUtils.getValue(annotation, "initScheduleTime").toString();
                    break;
                case WORKER:
                    scheduleTimeExpression = AnnotationUtils.getValue(annotation, "workerScheduleTime").toString();
                    break;
            }

            if(scheduleTimeExpression != null && scheduleTimeExpression.length() > 0) {
                CronExpression ce = CronExpression.parse(scheduleTimeExpression);
                LocalDateTime next = ce.next(LocalDateTime.now());
                return next.toInstant(ZoneOffset.UTC);
            }

            String secondsDelay = AnnotationUtils.getValue(annotation, "secondsDelay").toString();
            if(secondsDelay != null && !secondsDelay.equals("0")) {
                Long seconds = Long.valueOf(secondsDelay);
                if(nextScheduleTime != null) {
                    return nextScheduleTime.plusSeconds(seconds);
                } else {
                    return Instant.now().plusSeconds(seconds);
                }
            }
        }


        return nextScheduleTime;
    }

    private List<String> getArgumentsAsList(Map<String, String> arguments) {
        List<String> args = new ArrayList<>();
        arguments.keySet().forEach(key -> {
            args.add(key + "=" + arguments.get(key));
        });
        return args;
    }

    private void storeJobConfig(boolean isCharacterBased) {
        JobConfigDTO jobConfigDTO = new JobConfigDTO();
        jobConfigDTO.setName(getJobName());
        jobConfigDTO.setImage(image);
        jobConfigDTO.setVersion(buildProperties.getVersion());
        jobConfigDTO.setBuildTime(buildProperties.getTime());
        jobConfigDTO.setCharacterBased(isCharacterBased);
        jobConfigService.createJobConfig(jobConfigDTO);
    }

    protected <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    protected <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

}
