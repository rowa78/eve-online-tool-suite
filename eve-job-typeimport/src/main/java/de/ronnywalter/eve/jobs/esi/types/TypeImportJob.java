package de.ronnywalter.eve.jobs.esi.types;

import de.ronnywalter.eve.dto.*;
import de.ronnywalter.eve.jobs.AbstractJob;
import de.ronnywalter.eve.jobs.SchedulableJob;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import de.ronnywalter.eve.rest.TypeCategoryService;
import de.ronnywalter.eve.rest.TypeGroupService;
import de.ronnywalter.eve.rest.TypeService;
import de.ronnywalter.eve.rest.UniverseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CompletableFuture;

@SpringBootApplication (scanBasePackages = "de.ronnywalter.eve.jobs")
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
@EnableEurekaClient
@SchedulableJob(initScheduleTime = "0 30 11 * * *")
public class TypeImportJob extends EsiApiJob {

    private final UniverseApi universeApi;

    private final TypeCategoryService typeCategoryService;
    private final TypeGroupService typeGroupService;
    private final TypeService typeService;

    private final BuildProperties buildProperties;

    public static void main(String[] args) {
        SpringApplication.run(TypeImportJob.class, args);
    }

    protected void runInitJob(ApplicationArguments args) {

        List<Integer> categoryIds = getCategoryIds();

        getCategoryIds().forEach(categoryId -> {
            List<String> arguments = new ArrayList<>();
            arguments.add("--type.category.id=" + categoryId);
            scheduleTask(getJobName() + "-category-" + categoryId, AbstractJob.WORKER, arguments, Instant.now());
        });
        scheduleTask(getTaskName(), AbstractJob.INIT, new ArrayList<>(), getNextScheduleTime());
    }

    protected void runWorkerJob(ApplicationArguments args) {

        if(args.containsOption("type.category.id")) {
            List<Integer> categoryIds = getCategoryIds();
            List<String> text = args.getOptionValues("type.category.id");


            text.forEach(s -> {
                Integer typeCategory = Integer.parseInt(s);
                if(categoryIds.contains(typeCategory)) {
                    log.info("processing type.category.id " + s);
                    processTypeCategory(typeCategory);
                    log.info("finished, rescheduling job.");
                    List<String> arguments = new ArrayList<>();
                    arguments.add("--type.category.id=" + typeCategory);
                    arguments.add("--etag=" + getEtag());
                    scheduleTask(getJobName() + "-category-" + typeCategory, AbstractJob.WORKER, arguments, getNextScheduleTime());
                } else {
                    log.error("Category " + typeCategory + " does not exist anymore.");
                    this.setScheduleNextRun(false);
                }
            });
        }
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }

    private List<Integer> getCategoryIds() {
        List<Integer> categoryIds = new ArrayList<>();
        ResponseEntity<List<Integer>> categoryIdResponse = universeApi.getUniverseCategoriesWithHttpInfo(DATASOURCE, null);
        categoryIds.addAll(categoryIdResponse.getBody());
        return categoryIds;
    }

    private void processTypeCategory(Integer id) {
        String etag = getEtag();
        ResponseEntity<GetUniverseCategoriesCategoryIdOk> response = update(new Update<ResponseEntity<GetUniverseCategoriesCategoryIdOk>>() {
            @Override
            public ResponseEntity<GetUniverseCategoriesCategoryIdOk> update() throws HttpClientErrorException {
                return universeApi.getUniverseCategoriesCategoryIdWithHttpInfo(id, LANGUAGE, DATASOURCE, etag, LANGUAGE);
            }
        });

        if (response != null) {
            this.setEtag(getETAG(response.getHeaders()));
            this.setNextScheduleTime(getExpiryDate(response.getHeaders()));
            if (response.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                log.info("Content not modified (304)");
            } else {

                TypeCategoryDTO c = new TypeCategoryDTO();
                c.setId(id);
                c.setName(response.getBody().getName());
                typeCategoryService.createTypeCategory(c);

                for (Integer groupId : response.getBody().getGroups()) {
                    updateTypeGroup(groupId, c.getId());
                }

                log.info("finished category: " + c.getName());
            }
        }
    }

    private void updateTypeGroup(Integer groupId, int categoryId) {
        GetUniverseGroupsGroupIdOk response = update(new Update<GetUniverseGroupsGroupIdOk>() {
            @Override
            public GetUniverseGroupsGroupIdOk update() throws HttpClientErrorException {
                return universeApi.getUniverseGroupsGroupId(groupId, LANGUAGE, DATASOURCE, null, LANGUAGE);
            }
        });

        TypeGroupDTO tg = new TypeGroupDTO();
        tg.setId(groupId);

        tg.setCategoryId(categoryId);
        tg.setName(response.getName());
        typeGroupService.createTypeGroup(tg);

        response.getTypes().forEach(typeId -> {
            updateType(typeId, categoryId, groupId);
        });

        log.info("finished group: " + tg.getName());
    }

    private void updateType(Integer typeId, int catgoryId, int groupId) {
        ResponseEntity<GetUniverseTypesTypeIdOk> responseEntity = update(new Update<ResponseEntity<GetUniverseTypesTypeIdOk>>() {
            @Override
            public ResponseEntity<GetUniverseTypesTypeIdOk> update() throws HttpClientErrorException {
                return universeApi.getUniverseTypesTypeIdWithHttpInfo(typeId, LANGUAGE, DATASOURCE, null, LANGUAGE);
            }
        });

        TypeDTO type = new TypeDTO();
        type.setId(typeId);

        GetUniverseTypesTypeIdOk response = responseEntity.getBody();
        type.setId(response.getTypeId());
        type.setName(response.getName());
        try {
            type.setIconId(response.getIconId());
        } catch (NullPointerException e) {
            // NPE if there is no IconId
        }
        ;
        type.setPackagedVolume(response.getPackagedVolume());
        type.setVolume(response.getVolume());
        type.setPortionSize(response.getPortionSize());
        type.setGroupId(groupId);
        type.setCategoryId(catgoryId);
        type.setCapacity(response.getCapacity());
        type.setDescription(response.getDescription());
        type.setGraphicId(response.getGraphicId());
        type.setIconId(response.getIconId());
        type.setMass(response.getMass());
        type.setMarketGroupId(response.getMarketGroupId());
        type.setPublished(response.getPublished());
        type.setRadius(response.getRadius());
        typeService.createType(type);
    }

}
