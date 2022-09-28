package de.ronnywalter.eve.jobs.esi.corp;

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
@SchedulableJob(initScheduleTime = "* */30 * * * *")
public class CorpUpdateJob extends EsiApiJob {

    private final CharacterApi characterApi;
    private final CorporationApi corporationApi;

    private final CharacterService characterService;
    private final CorporationService corporationService;
    private final UserService userService;



    public static void main(String[] args) {
        SpringApplication.run(CorpUpdateJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<CorpDTO> corps = corporationService.getCorporations();
        log.info("Found " + corps.size() + " corps");
        corps.forEach(c ->{
                int corpId = c.getId();

                List<String> arguments = new ArrayList<>();
                arguments.add("--corp.id=" + corpId);

                log.info("Scheduling task for corp: " + c.getId());
                scheduleTask(getJobName() + "-" + c.getId(), AbstractJob.WORKER, arguments, Instant.now());
            });
        scheduleTask(getTaskName(), AbstractJob.INIT, new ArrayList<>(), getNextScheduleTime());
    }

    protected void runWorkerJob(ApplicationArguments args) {

        if(args.containsOption("corp.id")) {
            List<String> text = args.getOptionValues("corp.id");
            text.forEach(s -> {
                Integer corpId = Integer.parseInt(s);
                CorpDTO corp = corporationService.getCorporation(corpId);
                if(corp != null && corp.getId() != null) {
                    log.info("processing corp " + corp.getName());
                    CorpDTO c = processCorp(corp);
                    List<String> arguments = new ArrayList<>();
                    arguments.add("--corp.id=" + corpId);
                    arguments.add("--etag=" + getEtag());
                    scheduleTask(getJobName() + "-" + c.getTicker(), AbstractJob.WORKER, arguments, getNextScheduleTime());
                } else {
                    this.setScheduleNextRun(false);
                }
            });
        }
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return true;
    }

    private CorpDTO processCorp(CorpDTO c) {
        log.info("Updating corp: " + c.getName() + " (" + c.getId() + ")");
        String etag = getEtag();

        ResponseEntity<GetCorporationsCorporationIdOk> result = update(new Update<ResponseEntity<GetCorporationsCorporationIdOk>>() {
            @Override
            public ResponseEntity<GetCorporationsCorporationIdOk> update() throws HttpClientErrorException {
                return corporationApi.getCorporationsCorporationIdWithHttpInfo(c.getId(), DATASOURCE, etag);
            }
        });
        if (result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()));
            if(result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                c.setName(result.getBody().getName());
                c.setTicker(result.getBody().getTicker());
                c.setCeoId(result.getBody().getCeoId());
                c.setMemberCount(result.getBody().getMemberCount());
                ResponseEntity<GetCorporationsCorporationIdIconsOk> resultIcon = update(new Update<ResponseEntity<GetCorporationsCorporationIdIconsOk>>() {
                    @Override
                    public ResponseEntity<GetCorporationsCorporationIdIconsOk> update() throws HttpClientErrorException {
                        return corporationApi.getCorporationsCorporationIdIconsWithHttpInfo(c.getId(), DATASOURCE, null);
                    }
                });
                if(resultIcon != null) {
                    c.setLogo(resultIcon.getBody().getPx128x128());
                }
            }
            log.info("Storing corp: " + c);
            corporationService.updateCorporation(c.getId(), c);
        }
        return c;
    }
}
