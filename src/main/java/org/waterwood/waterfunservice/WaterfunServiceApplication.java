package org.waterwood.waterfunservice;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.resend.services.emails.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.waterwood.waterfunservice.confirguation.AliyunSmsConfig;
import org.waterwood.waterfunservice.entity.User.AccountStatus;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.utils.JsonUtil;

import java.util.Map;
import java.util.Optional;

import static com.aliyun.teautil.Common.toJSONString;

@SpringBootApplication
public class WaterfunServiceApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(WaterfunServiceApplication.class, args);
        System.out.println("========================");
        System.out.println("WaterFun Service Started");
        System.out.println("========================");
        System.out.println("1");

    }

}
