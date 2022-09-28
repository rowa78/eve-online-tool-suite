package de.ronnywalter.eve.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
public class K8sSchedulerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(K8sSchedulerApplication.class, args);
    }

}
 