package de.ronnywalter.eve.jobs.esi.structures;

import de.ronnywalter.eve.dto.EveCharacterDTO;
import de.ronnywalter.eve.dto.LocationDTO;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.CharacterService;
import de.ronnywalter.eve.rest.TokenService;
import de.ronnywalter.eve.rest.UniverseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.MarketApi;
import net.evetech.esi.client.api.UniverseApi;
import net.evetech.esi.client.model.GetMarketsStructuresStructureId200Ok;
import net.evetech.esi.client.model.GetUniverseStructuresStructureIdOk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SpringBootApplication (scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@SchedulableJob(initScheduleTime = "0 40 11 * * *", workerScheduleTime = "0 45 11 * * *")
public class StructureImportJob extends EsiApiJob {

    private final UniverseApi universeApi;
    private final MarketApi marketApi;

    private final CharacterService characterService;
    private final TokenService tokenService;
    private final UniverseService universeService;


    @Value("${structures.file}")
    private String structuresFile;

    private final BuildProperties buildProperties;

    public static void main(String[] args) {
        SpringApplication.run(StructureImportJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<String> arguments = new ArrayList<>();
        scheduleTask(getJobName() + "-worker", AbstractJob.WORKER, arguments, Instant.now());
        scheduleTask(getTaskName(), AbstractJob.INIT, arguments, getNextScheduleTime());
    }

    protected void runWorkerJob(ApplicationArguments args) {

        ResponseEntity<Set<Long>> response = update(new Update<ResponseEntity<Set<Long>>>() {
            @Override
            public ResponseEntity<Set<Long>> update() throws HttpClientErrorException {
                return universeApi.getUniverseStructuresWithHttpInfo(DATASOURCE, "market", null);
            }
        });

        log.info("Found " + response.getBody().size() + " structure ids.");

        Set<Long> structureIdsFromApi = response.getBody();
        Set<Long> structureIdsFromFile = getStructureIdsFromFile();

        List<LocationDTO> knownStructures = universeService.getstructures();
        Set<Long> forbiddenStructureIds = new HashSet<>();
        knownStructures.forEach(d ->{
            if(d.getAccessForbidden()) {
                forbiddenStructureIds.add(d.getId());
            }
        });

        Set<Long> knownIds = new HashSet<>();
        knownStructures.forEach(s -> {
            knownIds.add(s.getId());
        });

        // TODO: move to configfile
        EveCharacterDTO eveCharacter = characterService.getCharacter(1276540478);
        if(eveCharacter != null && eveCharacter.getId() != null) {

            Set<Long> idsToProcess = new HashSet<>();

            // process IDs from file, that we dont know
            structureIdsFromFile.forEach(i -> {
                if(!knownIds.contains(i)) {
                    idsToProcess.add(i);
                }
            });

            // process all IDs from api, if access is not forbidden (404)
            structureIdsFromApi.forEach(i -> {
                if(!forbiddenStructureIds.contains(i)) {
                    idsToProcess.add(i);
                }
            });

            // process all known IDs, if access is not forbidden
            knownIds.forEach(i -> {
                if(!forbiddenStructureIds.contains(i)) {
                    idsToProcess.add(i);
                }
            });

            log.info("Updating " + idsToProcess.size() + " structures.");

            idsToProcess.forEach(i -> {
                updateStructure(i, eveCharacter.getId());
            });
            scheduleTask(getTaskName(), AbstractJob.WORKER, new ArrayList<>(), getNextScheduleTime());
        }
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }

    public void updateStructure(long id, int characterId) {
        log.info("getting structure " + id);

        ResponseEntity<GetUniverseStructuresStructureIdOk> struc = update(new Update<ResponseEntity<GetUniverseStructuresStructureIdOk>>() {
              @Override
              public ResponseEntity<GetUniverseStructuresStructureIdOk> update() throws HttpClientErrorException {
                  return universeApi.getUniverseStructuresStructureIdWithHttpInfo(id, DATASOURCE, null, tokenService.getAccessToken(characterId));
              }
          }
        );

        ResponseEntity<List<GetMarketsStructuresStructureId200Ok>> strucMarket = update(new Update<ResponseEntity<List<GetMarketsStructuresStructureId200Ok>>>() {
                @Override
                public ResponseEntity<List<GetMarketsStructuresStructureId200Ok>> update() throws HttpClientErrorException {
                    return marketApi.getMarketsStructuresStructureIdWithHttpInfo(id, DATASOURCE, null, 1, tokenService.getAccessToken(characterId));
                }
            }
        );

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(id);
        locationDTO.setLocationType(LocationDTO.STRUCTURE);

        if (struc != null) {
            if(struc.getStatusCodeValue() == 403) {
                log.info("structure " + id + " is forbidden for user " + characterId);
                locationDTO.setAccessForbidden(true);
                locationDTO.setName("unknown");
                try {
                    // sleeping a moment because of the error rate limit of api
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(struc.getStatusCodeValue() == 404) {
                log.info("structure " + id + " not found on api.");
                // deleting it.
                try {
                    // sleeping a moment because of the error rate limit of api
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (struc.getBody() != null) {
                GetUniverseStructuresStructureIdOk s = struc.getBody();
                locationDTO.setSolarsystemId(struc.getBody().getSolarSystemId());
                locationDTO.setName(struc.getBody().getName());
                locationDTO.setAccessForbidden(false);
                locationDTO.setHasMarket(true);
                locationDTO.setOwnerCorpId(struc.getBody().getOwnerId());
                locationDTO.setTypeId(struc.getBody().getTypeId());
                log.info("Got structure: " + locationDTO.getName() + " (" + locationDTO.getId() + ")");
                if (strucMarket.getStatusCodeValue() == 403) {
                    log.info("structure " + locationDTO.getId() + " has no market access.");
                    locationDTO.setHasMarket(false);
                    try {
                        // sleeping a moment because of the error rate limit of api
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        universeService.savestructure(locationDTO);
    }

    private Set<Long> getStructureIdsFromFile() {
        Set<Long> structureIds = new HashSet<>();
        if(structuresFile != null) {
            List<String> ids = new ArrayList<>();
            ClassPathResource resource = new ClassPathResource(structuresFile);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                reader.lines().forEach(line -> {
                    ids.add(line);
                });
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log.info("got " + ids.size() + " structure-ids from file");

            int count = 0;
            for (String structureId : ids) {
                count++;
                log.info("processing structure " + count + "/" + ids.size());
                Long sId = Long.parseLong(structureId);
                structureIds.add(sId);
            }
        }
        return structureIds;
    }
}
