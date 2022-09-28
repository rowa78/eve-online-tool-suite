package de.ronnywalter.eve.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eve.esi")
@Getter
@Setter
public class EveEsiProperties {

    @Value( "${eve.esi.clientId}" )
    private String clientId;

    @Value( "${eve.esi.clientSecret}" )
    private String clientSecret;

    //@Value( "${eve.esi.redirect-uri}" )
    //private String redirectUrl;

    @Value( "${eve.esi.authorization-uri}" )
    private String authorizationUrl;


    @Value( "${eve.esi.token-uri}" )
    private String tokenUrl;
/*
    @Value( "${eve.esi.user-info-uri}" )
    private String verifyUrl;

    private List<String> scope;
*/
}
