package de.ronnywalter.eve.jobs;

import de.ronnywalter.eve.dto.MarketOrderDTO;
import de.ronnywalter.eve.jobs.esi.EsiApiJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.evetech.esi.client.api.MarketApi;
import net.evetech.esi.client.model.GetMarketsRegionIdOrders200Ok;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class JobTest extends EsiApiJob implements ApplicationRunner {

    private final MarketApi marketApi;

    private final ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        SpringApplication.run(JobTest.class, args);
    }


    public void run(ApplicationArguments arguments) throws Exception {
        log.info("Starting....");


        runWorkerJob(null);


        log.info("Finishing....");
        ctx.close();
    }

    @Override
    protected void runInitJob(ApplicationArguments args) {

    }

    @Override
    protected void runWorkerJob(ApplicationArguments args) {
        int regionId = 10000002;


        List<GetMarketsRegionIdOrders200Ok> marketOrdersResponses = updatePages(3, new EsiApiJob.EsiPagesHandler<GetMarketsRegionIdOrders200Ok>() {
            @Override
            public ResponseEntity<List<GetMarketsRegionIdOrders200Ok>> get(Integer page) throws HttpClientErrorException {
                return marketApi.getMarketsRegionIdOrdersWithHttpInfo("all", regionId, DATASOURCE, null, page, null);
            }
        });



        List<MarketOrderDTO> marketOrders = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        log.info("got " + marketOrdersResponses.size() + " orders.");
    }

    @Override
    protected boolean isCharacterBasedJob() {
        return false;
    }
}
