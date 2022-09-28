package de.ronnywalter.eve.frontend;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Theme(value = "eveonlinetoolsuite", variant = Lumo.DARK)
@PWA(name = "Eve Online Tool Suite", shortName = "Eve Online Tool Suite", offlineResources = {"images/logo.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
//@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients (basePackages = "de.ronnywalter.eve.rest")
public class Frontend extends SpringBootServletInitializer implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Frontend.class, args);
    }
}
