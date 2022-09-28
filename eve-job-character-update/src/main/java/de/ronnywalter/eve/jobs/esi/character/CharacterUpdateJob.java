package de.ronnywalter.eve.jobs.esi.character;

import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.CharacterApi;
import net.evetech.esi.client.api.CorporationApi;
import net.evetech.esi.client.api.UniverseApi;
import net.evetech.esi.client.model.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
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
@SchedulableJob(initScheduleTime = "0 30 11 * * *")
public class CharacterUpdateJob extends EsiApiJob {

    private final CharacterApi characterApi;
    private final CorporationApi corporationApi;

    private final CharacterService characterService;
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(CharacterUpdateJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<EveCharacterDTO> chars = characterService.getCharacters();
        log.info("Found " + chars.size() + " chars");
            chars.forEach(c ->{
                int charId = c.getId();

                List<String> arguments = new ArrayList<>();
                arguments.add("--character.id=" + charId);

                log.info("Scheduling task for char: " + c.getName());
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
        log.info("Updating character: " + c.getName() + " (" + c.getId() + ")");
        String etag = getEtag();
        ResponseEntity<GetCharactersCharacterIdOk> result = update(new Update<ResponseEntity<GetCharactersCharacterIdOk>>() {
            @Override
            public ResponseEntity<GetCharactersCharacterIdOk> update() throws HttpClientErrorException {
                return characterApi.getCharactersCharacterIdWithHttpInfo(c.getId(), DATASOURCE, etag);
            }
        });

        if (result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()).plusSeconds(1));

            if(result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                GetCharactersCharacterIdOk x = result.getBody();
                c.setName(x.getName());
                c.setAllianceId(x.getAllianceId() != null ? x.getAllianceId() : null);
                c.setCorporationId(x.getCorporationId());
                c.setSecurityStatus(x.getSecurityStatus());

                GetCorporationsCorporationIdOk corpData = update(new Update<GetCorporationsCorporationIdOk>() {
                    @Override
                    public GetCorporationsCorporationIdOk update() throws HttpClientErrorException {
                        return corporationApi.getCorporationsCorporationId(c.getCorporationId(), DATASOURCE, null);
                    }
                });

                if (corpData != null) {
                    c.setCorporationName(corpData.getName());
                    c.setCorporationTicker(corpData.getTicker());
                }

                ResponseEntity<GetCharactersCharacterIdPortraitOk> portrait = update(new Update<ResponseEntity<GetCharactersCharacterIdPortraitOk>>() {
                    @Override
                    public ResponseEntity<GetCharactersCharacterIdPortraitOk> update() throws HttpClientErrorException {
                        return characterApi.getCharactersCharacterIdPortraitWithHttpInfo(c.getId(), DATASOURCE, null);
                    }
                });
                c.setPortrait64(portrait.getBody().getPx64x64());
                c.setPortrait128(portrait.getBody().getPx128x128());
                c.setPortrait256(portrait.getBody().getPx256x256());
                c.setPortrait512(portrait.getBody().getPx512x512());

                ResponseEntity<GetCorporationsCorporationIdIconsOk> resultIcon = update(new Update<ResponseEntity<GetCorporationsCorporationIdIconsOk>>() {
                    @Override
                    public ResponseEntity<GetCorporationsCorporationIdIconsOk> update() throws HttpClientErrorException {
                        return corporationApi.getCorporationsCorporationIdIconsWithHttpInfo(c.getCorporationId(), DATASOURCE, null);
                    }
                });
                if (resultIcon != null) {
                    c.setCorpLogo(resultIcon.getBody().getPx128x128());
                }
                log.info("Storing character: " + c);
                characterService.updateCharacter(c.getId(), c);
            }
        }
    }
}
