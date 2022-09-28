package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.JobDataDTO;
import de.ronnywalter.eve.dto.JobDefinitionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eve-backend/jobs", decode404 = true)
public interface JobService {

    @PostMapping(value = "/schedule")
    JobDefinitionDTO scheduleJob(@RequestBody JobDefinitionDTO jobDefinitionDTO);

}
