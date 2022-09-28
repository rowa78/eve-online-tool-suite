package de.ronnywalter.eve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@EnableFeignClients
//@ComponentScan(basePackages = {"de.ronnywalter.eve", "de.ronnywalter.eve.esi.client"})
public class Backend extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Backend.class, args);
    }

}
 