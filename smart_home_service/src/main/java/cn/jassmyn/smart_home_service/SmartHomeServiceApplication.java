package cn.jassmyn.smart_home_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartHomeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHomeServiceApplication.class, args);
    }

}
