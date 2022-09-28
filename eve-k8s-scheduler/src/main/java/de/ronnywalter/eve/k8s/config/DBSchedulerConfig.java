package de.ronnywalter.eve.k8s.config;

import com.github.kagkarlsson.scheduler.boot.config.DbSchedulerCustomizer;
import com.github.kagkarlsson.scheduler.serializer.JacksonSerializer;
import com.github.kagkarlsson.scheduler.serializer.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class DBSchedulerConfig {

    @Bean
    public DbSchedulerCustomizer getDbSchedulerCustomizer() {
        return new DbSchedulerCustomizer() {
            @Override
            public Optional<Serializer> serializer() {
                return Optional.of(new JacksonSerializer());
            }
        };
    }
}
