package de.ronnywalter.eve.jobs.esi.character;

import com.netflix.discovery.converters.Auto;
import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.*;
import net.evetech.esi.client.model.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.*;

@SpringBootApplication (scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@EnableEurekaClient
@SchedulableJob(initScheduleTime = "* */30 * * * *", secondsDelay = "300")
public class CharacterLocationUpdate extends EsiApiJob{

    private final LocationApi locationApi;

    private final CharacterService characterService;
    private final UserService userService;
    private final TokenService tokenService;

    private final BuildProperties buildProperties;


    private ApplicationContext ctx;

    @Autowired
    private ConnectionFactory connectionFactory;

    public static void main(String[] args) {
        SpringApplication.run(CharacterLocationUpdate.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<EveCharacterDTO> chars = characterService.getCharacters();

        chars.forEach(c ->{
            log.info("found character: " + c.getName());
            int charId = c.getId();

            List<String> arguments = new ArrayList<>();
            arguments.add("--character.id=" + charId);

            scheduleTask(getJobName() + "-" + c.getName(), AbstractJob.WORKER, arguments, Instant.now());
        });

        scheduleTask(getTaskName(), AbstractJob.INIT, new ArrayList<>(), getNextScheduleTime());
    }

    protected void runWorkerJob(ApplicationArguments args) {

        if(args.containsOption("character.id")) {
            List<String> text = args.getOptionValues("character.id");
            text.forEach(s -> {
                Integer charId = Integer.parseInt(s);
                EveCharacterDTO character = characterService.getCharacter(charId);
                if(character != null && character.getId() != null) {
                    log.info("processing char " + character.getName());
                    processCharacter(character);

                    log.info("finished, rescheduling job.");
                    List<String> arguments = new ArrayList<>();
                    arguments.add("--character.id=" + charId);
                    arguments.add("--etag=" + getEtag());
                    scheduleTask(getJobName() + "-" + character.getName(), AbstractJob.WORKER, arguments, getNextScheduleTime());
                } else {
                    log.error("Character " + charId + " not found.");
                }
            });
        }
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return true;
    }


    private void processCharacter(EveCharacterDTO c) {
        log.info("Updating character-location: " + c.getName() + " (" + c.getId() + ")");
        String etag = getEtag();
        ResponseEntity<GetCharactersCharacterIdLocationOk> result = update(new Update<ResponseEntity<GetCharactersCharacterIdLocationOk>>() {
            @Override
            public ResponseEntity<GetCharactersCharacterIdLocationOk> update() throws RestClientException {
                return locationApi.getCharactersCharacterIdLocationWithHttpInfo(c.getId(), DATASOURCE, etag, tokenService.getAccessToken(c.getId()));
            }
        });

        if (result != null) {
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()));
            this.setEtag(getETAG(result.getHeaders()));

            if(result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                log.info("Storing new location");
                CharacterLocationDTO characterLocationDTO = new CharacterLocationDTO();
                characterLocationDTO.setId(c.getId());

                Integer stationId = result.getBody().getStationId();
                Long structureId = result.getBody().getStructureId();
                Long locationId = null;
                if (structureId != null) {
                    locationId = structureId;
                } else if (stationId != null) {
                    locationId = Long.valueOf(stationId);
                }
                if (locationId != null) {
                    characterLocationDTO.setLocationId(locationId);
                    log.info("Location of character: " + c.getName() + ": " + characterLocationDTO.getLocationId());
                    characterService.updateLocation(c.getId(), characterLocationDTO);
                }
            }
        }
    }
}
