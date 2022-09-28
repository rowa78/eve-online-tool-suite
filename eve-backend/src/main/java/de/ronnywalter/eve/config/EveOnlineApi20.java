package de.ronnywalter.eve.config;

import com.github.scribejava.core.builder.api.DefaultApi20;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EveOnlineApi20 extends DefaultApi20 {

    private final EveEsiProperties eveEsiProperties;

    @Override
    public String getAccessTokenEndpoint() {
        return "https://login.eveonline.com/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://login.eveonline.com/oauth/authorize";
    }
}
