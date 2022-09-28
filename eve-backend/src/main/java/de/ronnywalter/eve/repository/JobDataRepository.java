package de.ronnywalter.eve.repository;

import de.ronnywalter.eve.model.JobData;
import de.ronnywalter.eve.model.JobLog;
import org.springframework.data.repository.CrudRepository;


public interface JobDataRepository extends CrudRepository<JobData, String> {
}
