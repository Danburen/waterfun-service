package org.waterwood.waterfunservice;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.resend.services.emails.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.waterwood.waterfunservice.entity.User.AccountStatus;
import org.waterwood.waterfunservice.service.EmailService;

@SpringBootApplication
public class WaterfunServiceApplication {


    public static void main(String[] args) throws ResendException {
        SpringApplication.run(WaterfunServiceApplication.class, args);
        System.out.println("========================");
        System.out.println("WaterFun Service Started");
        System.out.println("========================");
        System.out.println("1");
    }

}
