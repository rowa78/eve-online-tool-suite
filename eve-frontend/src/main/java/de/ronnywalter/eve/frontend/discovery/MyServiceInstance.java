package de.ronnywalter.eve.frontend.discovery;

import lombok.*;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyServiceInstance implements ServiceInstance {

    private String serviceId;
    private String host;
    private int port;
    private boolean secure;
    private URI uri;
    private Map<String, String> metadata;

}
