package de.ronnywalter.eve.jobs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.ronnywalter.eve.dto.queues.Queues;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Rabbitmq Stuff
    @Bean
    public MessageConverter converter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("rabbitmq");
        connectionFactory.setUsername("user");
        connectionFactory.setPassword("user");
        return connectionFactory;
    }

    @Bean
    public Declarables createEveQueues(){

        return new Declarables(
                new TopicExchange("eve-online-tool-suite"),
                new Queue(Queues.EVE_JOBS_SCHEDULE ),
                new Binding(Queues.EVE_JOBS_SCHEDULE, Binding.DestinationType.QUEUE, "eve-online-tool-suite", Queues.EVE_JOBS_SCHEDULE, null),

                new Queue(Queues.EVE_MARKETORDER_EVENTS ),
                new Binding(Queues.EVE_MARKETORDER_EVENTS, Binding.DestinationType.QUEUE, "eve-online-tool-suite", Queues.EVE_MARKETORDER_EVENTS, null)

        );
    }

}
