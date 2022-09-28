package de.ronnywalter.eve.controller;


import de.ronnywalter.eve.dto.JobDataDTO;
import de.ronnywalter.eve.dto.JobLogDTO;
import de.ronnywalter.eve.model.JobData;
import de.ronnywalter.eve.model.JobLog;
import de.ronnywalter.eve.service.JobDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/jobdata")
public class JobDataController extends AbstractController{

	private final JobDataService jobDataService;

	@RequestMapping(value = "/{jobId}", method = { RequestMethod.GET })
	public JobDataDTO getJobData(@PathVariable String jobId) {
		JobData jobData = jobDataService.getJobData(jobId);
		if(jobData != null) {
			JobDataDTO jobLogDTO = map(jobData, JobDataDTO.class);
			return jobLogDTO;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(value = "/{jobName}")
	public JobDataDTO saveJobData(@PathVariable String jobName, @RequestBody JobDataDTO jobDataDTO) {
		JobData jobData = map(jobDataDTO, JobData.class);
		return map(jobDataService.saveJobData(jobData), JobDataDTO.class);
	}
}
