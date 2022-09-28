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
import net.evetech.esi.client.api.WalletApi;
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
public class WalletUpdateJob extends EsiApiJob {

    private final WalletApi walletApi;

    private final CharacterService characterService;
    private final CorporationService corporationService;
    private final UserService userService;
    private final TokenService tokenService;

    private final BuildProperties buildProperties;

    public static void main(String[] args) {
        SpringApplication.run(WalletUpdateJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<EveCharacterDTO> chars = characterService.getCharacters();

        chars.forEach(c ->{
            int charId = c.getId();

            List<String> arguments = new ArrayList<>();
            arguments.add("--character.id=" + charId);

            scheduleTask(getJobName() + "-" + c.getName(), AbstractJob.WORKER, arguments, Instant.now());
        });

        List<CorpDTO> corps = corporationService.getCorporations();

        corps.forEach(c ->{
            int corpId = c.getId();

            List<String> arguments = new ArrayList<>();
            arguments.add("--corp.id=" + corpId);

            String valueString = c.getTicker()!=null?c.getTicker():c.getId().toString();
            scheduleTask(getJobName() + "-" + valueString, AbstractJob.WORKER, arguments, Instant.now());
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
        } else if(args.containsOption("corp.id")) {
            List<String> text = args.getOptionValues("corp.id");
            text.forEach(s -> {
                Integer corpId = Integer.parseInt(s);
                CorpDTO corp = corporationService.getCorporation(corpId);
                if(corp != null && corp.getId() != null) {
                    log.info("processing corp " + corp.getTicker());
                    processCorp(corp);
                    List<String> arguments = new ArrayList<>();
                    arguments.add("--corp.id=" + corpId);
                    arguments.add("--etag=" + getEtag());
                    scheduleTask(getJobName() + "-" + corp.getTicker(), AbstractJob.WORKER, arguments, getNextScheduleTime());
                } else {
                    log.error("Corp " + corpId + " not found.");
                }

            });
        }
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return true;
    }

    private void processCorp(CorpDTO c) {
        log.info("Updating corp-wallet: " + c.getTicker() + " (" + c.getId() + ")");
        String etag = getEtag();

        ResponseEntity<List<GetCorporationsCorporationIdWallets200Ok>> result = update(new Update<ResponseEntity<List<GetCorporationsCorporationIdWallets200Ok>>>() {
            @Override
            public ResponseEntity<List<GetCorporationsCorporationIdWallets200Ok>> update() throws HttpClientErrorException {
                return walletApi.getCorporationsCorporationIdWalletsWithHttpInfo(c.getId(), DATASOURCE, etag, tokenService.getAccessToken(c.getUserId()));
            }
        });

        if (result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()).plusSeconds(1));

            if (result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                List<GetCorporationsCorporationIdWallets200Ok> wallets = result.getBody();
                wallets.forEach(w -> {
                    CorpWalletDTO dto = new CorpWalletDTO();
                    dto.setCorpId(c.getId());
                    dto.setDivision(w.getDivision());
                    dto.setValue(w.getBalance());
                    log.info("Saving wallet: " + dto);
                    corporationService.updateWallet(c.getId(), dto.getDivision(), dto);
                });
            }
        }
    }


    private void processCharacter(EveCharacterDTO c) {
        log.info("Updating character-wallet: " + c.getName() + " (" + c.getId() + ")");
        String etag = getEtag();

        ResponseEntity<Double> result = update(new Update<ResponseEntity<Double>>() {
            @Override
            public ResponseEntity<Double> update() throws HttpClientErrorException {
                return walletApi.getCharactersCharacterIdWalletWithHttpInfo(c.getId(), DATASOURCE, etag, tokenService.getAccessToken(c.getId()));
            }
        });

        if (result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()).plusSeconds(1));

            if (result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                if (result != null) {
                    CharacterWalletDTO w = new CharacterWalletDTO();
                    w.setCharacterId(c.getId());
                    w.setValue(result.getBody());
                    log.info("Wallet of character: " + c.getName() + ": " + w.getValue());
                    characterService.updateWallet(c.getId(), w);
                }
            }
        }
    }
}
