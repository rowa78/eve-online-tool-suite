package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.JobDefinitionDTO;
import de.ronnywalter.eve.model.JobDefinition;
import de.ronnywalter.eve.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping(value = "schedule")
    public void scheduleJob(@RequestBody JobDefinitionDTO jobDefinitionDTO) {

        log.info("Scheduling job: " + jobDefinitionDTO);
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName(jobDefinitionDTO.getName());
        jobDefinition.setJobName(jobDefinitionDTO.getJobName());
        jobDefinition.setArguments(jobDefinitionDTO.getArguments());

        jobService.scheduleTask(jobDefinition);
    }


    @GetMapping (value = "trigger")
    public void trigger() {
        rabbitTemplate.convertAndSend("eve-online-tool-suite", "eve.jobs.schedule", "Testtrigger");
    }

}
