package de.ronnywalter.eve.repository;

import de.ronnywalter.eve.model.JobConfig;
import de.ronnywalter.eve.model.JobLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface JobConfigRepository extends CrudRepository<JobConfig, String> {
    public List<JobConfig> findByCharacterBasedTrue();
}
