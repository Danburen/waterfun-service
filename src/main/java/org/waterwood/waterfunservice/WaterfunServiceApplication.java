package org.waterwood.waterfunservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.service.Impl.SpringEmailService;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;

@SpringBootApplication
public class WaterfunServiceApplication {


    public static void main(String[] args) throws Exception {
        System.out.println("========================");
        System.out.println("WaterFun Service Started");
        System.out.println("========================");
        ConfigurableApplicationContext context = SpringApplication.run(WaterfunServiceApplication.class, args);
    }
}
