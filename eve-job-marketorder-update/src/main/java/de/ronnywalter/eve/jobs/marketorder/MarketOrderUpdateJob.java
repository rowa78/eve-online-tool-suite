package de.ronnywalter.eve.jobs.marketorder;

import com.google.common.collect.Lists;
import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.dto.queues.Queues;
import de.ronnywalter.eve.events.MarketOrderUpdateEvent;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.MarketApi;
import net.evetech.esi.client.api.UniverseApi;
import net.evetech.esi.client.model.GetMarketsRegionIdOrders200Ok;
import net.evetech.esi.client.model.GetMarketsStructuresStructureId200Ok;
import net.evetech.esi.client.model.GetUniverseRegionsRegionIdOk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Instant;
import java.util.*;

@SpringBootApplication(scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@EnableEurekaClient
@EnableDiscoveryClient
@SchedulableJob(initScheduleTime = "30 0 12 * * *")
public class MarketOrderUpdateJob extends EsiApiJob {

    private final MarketApi marketApi;
    private final UniverseApi universeApi;

    @Autowired
    private MarketOrderService marketOrderService;
    @Autowired
    private UniverseService universeService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CharacterService characterService;

    private Map<Integer, TypeDTO> types= new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(MarketOrderUpdateJob.class, args);
    }

    @Override
    protected void runInitJob(ApplicationArguments args) {
        List<Integer> regionsToProcess = getRegions();
        regionsToProcess.forEach(r -> {
            List<String> arguments = new ArrayList<>();
            arguments.add("--region.id=" + r);
            scheduleTask(getJobName() + "-region-" + r, AbstractJob.WORKER, arguments, Instant.now());
        });
        scheduleTask(getTaskName(), AbstractJob.INIT, new ArrayList<>(), getNextScheduleTime());
    }

    @Override
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
            }
        });
    }

    private void processRegion(Integer regionId) {
        log.info("Updating market orders for region: " + regionId);
        String etag = getEtag();

        log.info("Processing region: " + regionId);

        List<GetMarketsRegionIdOrders200Ok> marketOrdersFromApi = new ArrayList<>();

        // TODO: implement etag-caching
        ResponseEntity<List<GetMarketsRegionIdOrders200Ok>> result = update(new Update<ResponseEntity<List<GetMarketsRegionIdOrders200Ok>>>() {
            @Override
            public ResponseEntity<List<GetMarketsRegionIdOrders200Ok>> update() throws HttpClientErrorException {
                return marketApi.getMarketsRegionIdOrdersWithHttpInfo("all", regionId, DATASOURCE, etag, 1, null);
            }
        });

        if(result != null) {
            this.setEtag(getETAG(result.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(result.getHeaders()).plusSeconds(1));
            this.setLastModified(getLastModifiedDate(result.getHeaders()));
            int pages = getXPages(result.getHeaders());
            if(result.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {
                marketOrdersFromApi.addAll(result.getBody());

                if(pages > 1) {
                    List<GetMarketsRegionIdOrders200Ok> marketOrdersResponses = updatePages(3, new EsiApiJob.EsiPagesHandler<GetMarketsRegionIdOrders200Ok>() {
                        @Override
                        public ResponseEntity<List<GetMarketsRegionIdOrders200Ok>> get(Integer page) throws HttpClientErrorException {
                            return marketApi.getMarketsRegionIdOrdersWithHttpInfo("all", regionId, DATASOURCE, null, page, null);
                        }
                    }, 2);
                    marketOrdersFromApi.addAll(marketOrdersResponses);
                }
            }
        }

        List<MarketOrderDTO> marketOrders = new ArrayList<>();

        for (GetMarketsRegionIdOrders200Ok marketsRegionIdOrders200Ok : marketOrdersFromApi) {
            marketOrders.add(processMarketOrder(marketsRegionIdOrders200Ok, regionId));
        }

        log.info("Processed " + marketOrders.size() + " orders in region " + regionId);

        if(marketOrders.size() > 0) {
            MarketOrderUpdateEvent event = new MarketOrderUpdateEvent();
            event.setRegionId(regionId);
            event.setMarketOrderDTOS(marketOrders);
            event.setDate(getLastModified());
            getRabbitTemplate().convertAndSend(Queues.EVE_MARKETORDER_EVENTS, event);
        }

        // TODO: move to configfile
        EveCharacterDTO eveCharacter = characterService.getCharacter(1276540478);
        if(eveCharacter != null && eveCharacter.getId() != null) {
            int count = 0;
            List<LocationDTO> structures = universeService.getStructuresWithMarkets(regionId);
            structures.forEach(s -> {
                log.info("Getting orders for structure " + s.getId() + " (" + s.getName() + ")");
                List<GetMarketsStructuresStructureId200Ok> response = updatePages(3, new EsiApiJob.EsiPagesHandler<GetMarketsStructuresStructureId200Ok>() {
                    @Override
                    public ResponseEntity<List<GetMarketsStructuresStructureId200Ok>> get(Integer page) throws HttpStatusCodeException {
                        return marketApi.getMarketsStructuresStructureIdWithHttpInfo(s.getId(), DATASOURCE, null, page, tokenService.getAccessToken(eveCharacter.getId()));
                    }
                });
                response.forEach(getMarketsStructuresStructureId200Ok -> {
                    processStructureMarketOrder(getMarketsStructuresStructureId200Ok, s);
                });

            });
        }

    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }


    private void processMarketOrders(int regionId, List<MarketOrderDTO> newMarketOrders) {
        log.info("Process " + newMarketOrders.size() + " orders in region " + regionId);

        MarketOrderUpdateEvent event = new MarketOrderUpdateEvent();
        event.setRegionId(regionId);
        event.setMarketOrderDTOS(newMarketOrders);

    }


    /*
    @Job(name = "Update marketorders for in region %0", retries = 5)
    public void updateMarketOrdersForRegion(int regionId, int characterId, JobContext jobContext) throws OrderSetNotFoundException {

        OrderSet orderSet = new OrderSet();
        orderSet.setDate(LocalDateTime.now(ZoneOffset.UTC));
        if(regionId == 0) {
            List<Region> regions = universeService.getRegions();
            regions.forEach(r -> orderSet.addRegion(r.getId()));
        } else {
            orderSet.addRegion(regionId);
        }
        marketOrderService.saveOrderSet(orderSet);

        long orderSetId = orderSet.getId();
        final List<JobId> jobIds = new ArrayList<>();
        for(Integer r : orderSet.getRegions()) {
            List<Integer> typeIds = getActiveTypes(r);
            log.info("importing public orders for region " + r);
            for (Integer type : typeIds) {
                jobIds.add(jobScheduler.enqueue(() -> updateMarketOrdersForTypeAndRegion(orderSetId, r, type, characterId, JobContext.Null)));
            }
        }
        jobUtil.waitForJobExecution(jobIds);
        log.info("public orders imported");
    }*/

    private List<Integer> getActiveTypes(int regionId) {
        List<Integer> typeIds = new ArrayList<>();
        ResponseEntity<List<Integer>> response = marketApi.getMarketsRegionIdTypesWithHttpInfo(regionId, DATASOURCE, null, 1);
        Integer xPages = getXPages(response.getHeaders());
        typeIds.addAll(response.getBody());
        if (xPages != null && xPages > 1) {
            for (int i = 2; i <= xPages; i++) {
                List<Integer> ids = marketApi.getMarketsRegionIdTypes(regionId, DATASOURCE, null, i);
                typeIds.addAll(ids);
            }
        }
        return typeIds;
    }

    private MarketOrderDTO processMarketOrder(GetMarketsRegionIdOrders200Ok marketOrdersResponse, int regionId) {
        MarketOrderDTO mo = new MarketOrderDTO();
        mo.setOrderId(marketOrdersResponse.getOrderId());
        mo.setIssuedDate(marketOrdersResponse.getIssued().toLocalDateTime());
        mo.setBuyOrder(marketOrdersResponse.getIsBuyOrder());
        mo.setDuration(marketOrdersResponse.getDuration());
        mo.setVolumeTotal(marketOrdersResponse.getVolumeTotal());
        mo.setVolumeRemain(marketOrdersResponse.getVolumeRemain());
        mo.setPrice(marketOrdersResponse.getPrice());
        mo.setOrderRange(marketOrdersResponse.getRange().getValue());
        mo.setMinVolume(marketOrdersResponse.getMinVolume());
        mo.setRegionId(regionId);

        mo.setLocationId(marketOrdersResponse.getLocationId());
        mo.setTypeId(marketOrdersResponse.getTypeId());

        return mo;
    }

    private MarketOrderDTO processStructureMarketOrder(GetMarketsStructuresStructureId200Ok marketOrdersResponse, LocationDTO structure) {
        MarketOrderDTO mo = new MarketOrderDTO();
        mo.setOrderId(marketOrdersResponse.getOrderId());
        mo.setIssuedDate(marketOrdersResponse.getIssued().toLocalDateTime());
        mo.setBuyOrder(marketOrdersResponse.getIsBuyOrder());
        mo.setDuration(marketOrdersResponse.getDuration());
        mo.setVolumeTotal(marketOrdersResponse.getVolumeTotal());
        mo.setVolumeRemain(marketOrdersResponse.getVolumeRemain());
        mo.setPrice(marketOrdersResponse.getPrice());
        mo.setOrderRange(marketOrdersResponse.getRange().getValue());
        mo.setMinVolume(marketOrdersResponse.getMinVolume());
        mo.setLocationId(structure.getId());
        mo.setRegionId(structure.getRegionId());

        mo.setLocationId(marketOrdersResponse.getLocationId());
        mo.setTypeId(marketOrdersResponse.getTypeId());

        return mo;
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
}
