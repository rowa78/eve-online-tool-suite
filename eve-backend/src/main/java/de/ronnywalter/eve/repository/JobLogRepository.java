package de.ronnywalter.eve.repository;

import de.ronnywalter.eve.model.JobLog;
import org.springframework.data.repository.CrudRepository;


public interface JobLogRepository extends CrudRepository<JobLog, String> {
}
