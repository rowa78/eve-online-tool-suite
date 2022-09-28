package de.ronnywalter.eve.service;

import de.ronnywalter.eve.model.JobData;
import de.ronnywalter.eve.model.JobLog;
import de.ronnywalter.eve.repository.JobDataRepository;
import de.ronnywalter.eve.repository.JobLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobDataService {

    private final JobDataRepository jobDataRepository;

    public JobData saveJobData(JobData jobData) {
        return jobDataRepository.save(jobData);
    }

    public JobData getJobData (String id) {
        return jobDataRepository.findById(id).orElse(null);
    }

}
