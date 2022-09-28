package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.JobDataDTO;
import de.ronnywalter.eve.dto.JobLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eve-backend/jobdata", decode404 = true)
public interface JobDataService {

    @RequestMapping(method = RequestMethod.GET, value = "/{jobName}")
    JobDataDTO getJobData(@PathVariable("jobName") String jobId);

    @PutMapping(value = "/{jobName}")
    JobDataDTO saveJobData(@PathVariable("jobName") String jobId, @RequestBody JobDataDTO jobDataDTO);

}
