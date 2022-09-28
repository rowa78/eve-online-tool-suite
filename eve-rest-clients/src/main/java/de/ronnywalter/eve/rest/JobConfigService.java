package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.JobConfigDTO;
import de.ronnywalter.eve.dto.JobDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eve-backend/jobconfig", decode404 = true)
public interface JobConfigService {

    @RequestMapping(method = RequestMethod.GET, value = "/{jobName}")
    JobConfigDTO getJobConfig(@PathVariable("jobName") String jobId);

    @PutMapping(value = "/{jobName}")
    JobConfigDTO saveJobConfig(@PathVariable("jobName") String jobId, @RequestBody JobConfigDTO jobConfigDTO);

    @PostMapping()
    JobConfigDTO createJobConfig(@RequestBody JobConfigDTO jobConfigDTO);

}
