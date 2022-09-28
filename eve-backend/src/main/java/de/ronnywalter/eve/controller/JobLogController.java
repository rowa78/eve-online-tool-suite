package de.ronnywalter.eve.controller;


import de.ronnywalter.eve.dto.JobLogDTO;
import de.ronnywalter.eve.model.JobLog;
import de.ronnywalter.eve.service.JobLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/joblogs")
public class JobLogController extends AbstractController{

	private final JobLogService jobLogService;

	@RequestMapping(value = "/{jobId}", method = { RequestMethod.GET })
	public JobLogDTO getJobData(@PathVariable String jobId) {
		JobLog jobData = jobLogService.getJobData(jobId);
		if(jobData != null) {
			JobLogDTO jobLogDTO = map(jobData, JobLogDTO.class);
			return jobLogDTO;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping(value = "")
	public JobLogDTO saveJobData(@RequestBody JobLogDTO jobLogDTO) {
		JobLog jobData = map(jobLogDTO, JobLog.class);
		return map(jobLogService.saveJobLog(jobData), JobLogDTO.class);
	}
}
