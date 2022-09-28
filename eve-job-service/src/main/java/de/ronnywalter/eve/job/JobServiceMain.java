package de.ronnywalter.eve.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "de.ronnywalter.eve.rest")
public class JobServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(JobServiceMain.class, args);
    }

}
