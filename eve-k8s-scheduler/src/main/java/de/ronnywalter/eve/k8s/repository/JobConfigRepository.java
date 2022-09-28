package de.ronnywalter.eve.k8s.repository;

import de.ronnywalter.eve.k8s.model.JobConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface JobConfigRepository extends CrudRepository<JobConfig, String> {
    public List<JobConfig> findByCharacterBasedTrue();
}
