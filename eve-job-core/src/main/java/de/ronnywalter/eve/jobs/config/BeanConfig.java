package de.ronnywalter.eve.jobs.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.ronnywalter.eve.jobs.util.DefaultResponseErrorHandler;
import net.evetech.esi.client.ApiClient;
import net.evetech.esi.client.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ApiClient getApiClient(RestTemplate restTemplate) {
        return new ApiClient(restTemplate);
    }

    @Bean
    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .errorHandler(getErrorHandler()).build();
    }

    @Bean
    public DefaultResponseErrorHandler getErrorHandler() {
        return new DefaultResponseErrorHandler();
    }

    @Bean
    public UniverseApi getUniverseApi(ApiClient apiClient) {
        return new UniverseApi(apiClient);
    }

    @Bean
    public CharacterApi getCharacterApi(ApiClient apiClient) {
        return new CharacterApi(apiClient);
    }

    @Bean
    public CorporationApi getCorporationApi(ApiClient apiClient) {
        return new CorporationApi(apiClient);
    }

    @Bean
    public LocationApi getLocationApi(ApiClient apiClient) {
        return new LocationApi(apiClient);
    }

    @Bean
    public WalletApi getWalletApi(ApiClient apiClient) {
        return new WalletApi(apiClient);
    }

    @Bean
    public MarketApi getMarketApi(ApiClient apiClient) {
        return new MarketApi(apiClient);
    }




}
