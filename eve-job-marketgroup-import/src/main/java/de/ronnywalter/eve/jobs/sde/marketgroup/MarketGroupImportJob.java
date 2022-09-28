package de.ronnywalter.eve.jobs.sde.marketgroup;

import de.ronnywalter.eve.dto.JobConfigDTO;
import de.ronnywalter.eve.dto.JobDefinitionDTO;
import de.ronnywalter.eve.dto.MarketGroupDTO;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.rest.MarketGroupSDEService;
import de.ronnywalter.eve.rest.MarketGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.time.Instant;
import java.util.*;

@SpringBootApplication (scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@EnableEurekaClient
@EnableDiscoveryClient
@SchedulableJob(initScheduleTime = "0 30 11 * * *", workerScheduleTime = "0 35 11 * * *")
public class MarketGroupImportJob extends AbstractJob {

    private final MarketGroupService marketGroupService;
    private final MarketGroupSDEService marketGroupSDEService;
    private final BuildProperties buildProperties;

    public static void main(String[] args) {
        SpringApplication.run(MarketGroupImportJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {


        List<String> arguments = new ArrayList<>();

        scheduleTask(getJobName() + "-worker", AbstractJob.WORKER, arguments, Instant.now());
        scheduleTask(getTaskName(), AbstractJob.INIT, arguments, getNextScheduleTime());
    }

    protected void runWorkerJob(ApplicationArguments args) {
        List<MarketGroupDTO> marketGroupDTOS = marketGroupSDEService.getMarketGroups();
        marketGroupDTOS.forEach(m -> {
            log.info("Creating marketgroup: " + m.getName());
            marketGroupService.createMarketGroup(m);
        });
        scheduleTask(getTaskName(), AbstractJob.WORKER, new ArrayList<>(), getNextScheduleTime());
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }
}
