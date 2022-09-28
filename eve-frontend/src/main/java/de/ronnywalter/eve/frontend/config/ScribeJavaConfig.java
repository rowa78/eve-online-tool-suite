package de.ronnywalter.eve.frontend.config;

import com.github.scribejava.core.builder.ScopeBuilder;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "eve.esi")
public class ScribeJavaConfig {

    private final EveOnlineApi20 eveOnlineApi20;
    private final EveEsiProperties eveEsiProperties;

    @Bean
    public OAuth20Service getOauthService() {
        ScopeBuilder sb = new ScopeBuilder(eveEsiProperties.getScope());

        OAuth20Service service = new ServiceBuilder(eveEsiProperties.getClientId())
                .apiSecret(eveEsiProperties.getClientSecret())
                .callback(eveEsiProperties.getRedirectUrl())
                .defaultScope(sb)
                .build(eveOnlineApi20);
        return service;
    }

}