package de.ronnywalter.eve.k8s.controller;

import de.ronnywalter.eve.k8s.JobDefinitionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "job/schedule")
@RequiredArgsConstructor
public class JobScheduleController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public JobDefinitionDTO scheduleJob(@RequestBody JobDefinitionDTO job) {
        rabbitTemplate.convertAndSend("eve.jobs.schedule", job);
        return job;
    }




}
