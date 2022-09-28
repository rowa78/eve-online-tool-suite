package de.ronnywalter.eve.frontend.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@Component
public class LocalDiscoveryClient implements DiscoveryClient {

    @Override
    public String description() {
        return "local dev discovery-client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> instances = new ArrayList<>();

        ServiceInstance instance = null;
        try {
            instance = MyServiceInstance.builder()
                            .host("localhost")
                            .port(8080)
                            .serviceId("eve-backend")
                            .secure(false)
                            .metadata(null)
                            .uri(new URI("http://localhost:8080"))
                    .build();
            instances.add(instance);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return instances;
    }

    @Override
    public List<String> getServices() {
        List<String> services = new ArrayList<>();
        services.add("eve-backend");
        return services;
    }
}

