package de.ronnywalter.eve.jobs.esi.universe;

import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.JobDataService;
import de.ronnywalter.eve.rest.UniverseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.UniverseApi;
import net.evetech.esi.client.model.GetUniverseConstellationsConstellationIdOk;
import net.evetech.esi.client.model.GetUniverseRegionsRegionIdOk;
import net.evetech.esi.client.model.GetUniverseStationsStationIdOk;
import net.evetech.esi.client.model.GetUniverseSystemsSystemIdOk;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.*;

@SpringBootApplication (scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@EnableEurekaClient
@EnableDiscoveryClient
@SchedulableJob(initScheduleTime = "0 05 11 * * *")
public class UniverseImportJob extends EsiApiJob {

    private final UniverseApi universeApi;
    private final UniverseService universeService;

    public static void main(String[] args) {
        SpringApplication.run(UniverseImportJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<Integer> regionsToProcess = getRegions();
        regionsToProcess.forEach(r -> {
            List<String> arguments = new ArrayList<>();
            arguments.add("--region.id=" + r);
            scheduleTask(getJobName() + "-region-" + r, AbstractJob.WORKER, arguments, Instant.now());
        });
        scheduleTask(getTaskName(), AbstractJob.INIT, new ArrayList<>(), getNextScheduleTime());
    }

    private List<Integer> getRegions() {
        List<Integer> regionsToProcess = new ArrayList<>();
        ResponseEntity<List<Integer>> ids = update(new Update<ResponseEntity<List<Integer>>>() {
            @Override
            public ResponseEntity<List<Integer>> update() throws HttpClientErrorException {
                return universeApi.getUniverseRegionsWithHttpInfo(DATASOURCE, null);
            }
        });
        regionsToProcess.addAll(ids.getBody());
        return regionsToProcess;

    }

    protected void runWorkerJob(ApplicationArguments args) {

        List<String> text = args.getOptionValues("region.id");
        List<Integer> regions = getRegions();
        text.forEach(s -> {
            Integer regionId = Integer.parseInt(s);
            if(regions.contains(regionId)) {
                log.info("processing region " + s);
                processRegion(regionId);
                log.info("finished, rescheduling job.");
                List<String> arguments = new ArrayList<>();
                arguments.add("--region.id=" + regionId);
                arguments.add("--etag=" + getEtag());
                scheduleTask(getJobName() + "-region-" + regionId, AbstractJob.WORKER, arguments, getNextScheduleTime());
            } else {
                log.error("Region " + regionId + " does not exist anymore.");
                this.setScheduleNextRun(false);
            }
        });
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }

    private void processRegion(Integer regionId) {
        log.info("Updating region: " + regionId);
        String etag = getEtag();

        ResponseEntity<GetUniverseRegionsRegionIdOk> result = update(new Update<ResponseEntity<GetUniverseRegionsRegionIdOk>>() {
            @Override
            public ResponseEntity<GetUniverseRegionsRegionIdOk> update() throws HttpClientErrorException {
                return universeApi.getUniverseRegionsRegionIdWithHttpInfo(regionId, LANGUAGE,DATASOURCE, etag, LANGUAGE);
            }
        });

        if (result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()));
            if (result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {

                RegionDTO region = new RegionDTO();
                region.setId(result.getBody().getRegionId());
                region.setName(result.getBody().getName());
                region.setDescription(result.getBody().getDescription());

                universeService.saveRegion(region);

                for (Integer constellationId : result.getBody().getConstellations()) {
                    updateConstellation(constellationId, region.getId());
                }
            }
        }
    }

    private void updateConstellation(Integer constellationId, int regionId) {
        GetUniverseConstellationsConstellationIdOk response = update(new Update<GetUniverseConstellationsConstellationIdOk>() {
            @Override
            public GetUniverseConstellationsConstellationIdOk update() throws HttpClientErrorException {
                return universeApi.getUniverseConstellationsConstellationId(constellationId, LANGUAGE, DATASOURCE, null, LANGUAGE);
            }
        });

        ConstellationDTO constellation = new ConstellationDTO();
        constellation.setId(response.getConstellationId());
        constellation.setName(response.getName());
        constellation.setRegionId(regionId);
        universeService.saveConstellations(constellation);
        for(Integer s: response.getSystems()) {
            updateSystem(s, constellation.getId(), regionId);
        }
        log.info("Updated constellation: " + constellation.getName());
    }

    private void updateSystem(Integer systemId, int constellationId, int regionId) {
        GetUniverseSystemsSystemIdOk response = update(new Update<GetUniverseSystemsSystemIdOk>() {
            @Override
            public GetUniverseSystemsSystemIdOk update() throws HttpClientErrorException {
                return universeApi.getUniverseSystemsSystemId(systemId, LANGUAGE, DATASOURCE, null, LANGUAGE);
            }
        });

        SolarSystemDTO system = new SolarSystemDTO();
        system.setId(response.getSystemId());
        system.setConstellationId(constellationId);
        system.setRegionId(regionId);
        system.setName(response.getName());
        system.setSecurityStatus(response.getSecurityStatus());

        universeService.saveSolarSystem(system);

        if(response.getStations() != null) {
            for(Integer s : response.getStations()) {
                updateStation(s, systemId, constellationId, regionId);
            };
        }
        log.info("Updated solarsystem: " + system.getName());
    }

    private void updateStation(Integer stationId, int systemId, int constellationId, int regionID) {
        GetUniverseStationsStationIdOk response = update(new Update<GetUniverseStationsStationIdOk>() {
            @Override
            public GetUniverseStationsStationIdOk update() throws HttpClientErrorException {
                return universeApi.getUniverseStationsStationId(stationId, DATASOURCE, null);
            }
        });
        LocationDTO s = new LocationDTO();
        s.setId(Long.valueOf(stationId));
        s.setLocationType("station");
        s.setSolarsystemId(systemId);
        s.setConstellationId(constellationId);
        s.setRegionId(regionID);
        s.setName(response.getName());
        s.setOwnerCorpId(response.getOwner());
        s.setTypeId(response.getTypeId());
        log.info("Updated station: " + s.getName());
        universeService.saveStation(s);
    }


}
