package de.ronnywalter.eve.controller;


import de.ronnywalter.eve.dto.JobConfigDTO;
import de.ronnywalter.eve.dto.JobDataDTO;
import de.ronnywalter.eve.model.JobConfig;
import de.ronnywalter.eve.model.JobData;
import de.ronnywalter.eve.model.JobDefinition;
import de.ronnywalter.eve.service.JobDataService;
import de.ronnywalter.eve.service.JobRegistryService;
import de.ronnywalter.eve.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/jobconfig")
public class JobConfigController extends AbstractController{

	private final JobRegistryService jobRegistryService;
	private final JobService jobService;

	@RequestMapping(value = "/{jobName}", method = { RequestMethod.GET })
	public JobConfigDTO getJobData(@PathVariable String jobName) {
		JobConfig jobConfig = jobRegistryService.getJobConfig(jobName);
		if(jobConfig != null) {
			JobConfigDTO jobConfigDTO = map(jobConfig, JobConfigDTO.class);
			return jobConfigDTO;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(value = "/{jobName}")
	public JobConfigDTO saveJobData(@PathVariable String jobName, @RequestBody JobConfigDTO jobConfigDTO) {
		JobConfig jobConfig = jobConfig = jobRegistryService.saveJobConfig(map(jobConfigDTO, JobConfig.class));

		/*JobDefinition jd = new JobDefinition();
		jd.setName(jobConfig.getName());
		jd.setJobName(jobConfig.getName());
		List<String> arguments = new ArrayList<>();
		arguments.add("--mode=init");
		jd.setArguments(arguments);

		jobService.scheduleTask(jd);
		*/


		return map(jobConfig, JobConfigDTO.class);
	}

	@PostMapping()
	public JobConfigDTO createJobData(@RequestBody JobConfigDTO jobConfigDTO) {
		JobConfig jobConfig = jobConfig = jobRegistryService.saveJobConfig(map(jobConfigDTO, JobConfig.class));
/*
		JobDefinition jd = new JobDefinition();
		jd.setName(jobConfig.getName());
		jd.setJobName(jobConfig.getName());
		List<String> arguments = new ArrayList<>();
		arguments.add("--mode=init");
		jd.setArguments(arguments);

		jobService.scheduleTask(jd);


 */
		return map(jobConfig, JobConfigDTO.class);
	}
}
