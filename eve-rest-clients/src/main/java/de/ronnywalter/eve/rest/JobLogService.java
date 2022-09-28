package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.JobLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eve-backend/joblogs", decode404 = true)
public interface JobLogService {

    @RequestMapping(method = RequestMethod.GET, value = "/{jobId}")
    JobLogDTO getJobLogs(@PathVariable("jobId") int jobId);

    @PostMapping(value = "")
    JobLogDTO saveJobLogs(@RequestBody JobLogDTO jobLogDTO);

}
