package de.ronnywalter.eve.service;

import de.ronnywalter.eve.model.JobLog;
import de.ronnywalter.eve.repository.JobLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobLogService {

    private final JobLogRepository jobLogRepository;

    public JobLog saveJobLog(JobLog jobData) {
        return jobLogRepository.save(jobData);
    }

    public JobLog getJobData (String id) {
        return jobLogRepository.findById(id).orElse(null);
    }

}
