package de.ronnywalter.eve.k8s.service;

import com.google.common.collect.Lists;
import de.ronnywalter.eve.k8s.model.JobConfig;
import de.ronnywalter.eve.k8s.repository.JobConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRegistryService {

    private final JobConfigRepository jobConfigRepository;

    public JobConfig saveJobConfig(JobConfig jobConfig) {
        return jobConfigRepository.save(jobConfig);
    }

    public JobConfig getJobConfig (String id) {
        return jobConfigRepository.findById(id).orElse(null);
    }

    public List<JobConfig> getAllJobConfigs() { return Lists.newArrayList(jobConfigRepository.findAll());}

    public List<JobConfig> getCharacterBasedJobConfigs() {
        return jobConfigRepository.findByCharacterBasedTrue();
    }

}
