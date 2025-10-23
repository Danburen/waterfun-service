package org.waterwood.waterfunservice.service.Impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.service.SmsService;
import org.waterwood.waterfunservice.service.dto.SmsCodeResult;
import org.waterwood.waterfunservice.confirguation.AliyunSmsConfig;
import org.waterwood.waterfunservice.utils.JsonUtil;
import static com.aliyun.teautil.Common.toJSONString;

import java.util.Map;

@Service
@Slf4j
public class AliyunSmsService implements SmsService {
    private Client client;
    @Value("${aliyun.sms.sign-name}")
    private String signName;
    public AliyunSmsService() {
        try{
            client = AliyunSmsConfig.getClient();
        }catch (Exception e){
            log.error("Can't create Aliyun client instance{}", e.getMessage());
            client = null;
        }
    }

    @Override
    public SmsCodeResult sendSms(String phoneNumber, String templateCode, Map<String, Object> params) {
        if(client == null){
            log.error("Fail send Sms code to {},cause:{}",phoneNumber,"Can't get client instance");
            return SmsCodeResult.builder()
                    .sendSuccess(false)
                    .phoneNumber(phoneNumber)
                    .message("Can't get client instance")
                    .build();
        }
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(JsonUtil.toJson(params));
        try {
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            return SmsCodeResult.builder()
                    .sendSuccess(sendSmsResponse.getBody().getCode() != null && sendSmsResponse.getBody().getCode().equals("OK"))
                    .phoneNumber(phoneNumber)
                    .message(sendSmsResponse.getBody().getMessage())
                    .responseRaw(toJSONString(sendSmsResponse))
                    .build();
        }catch (Exception e){
            log.error("Fail send Sms code to {},cause:{}",phoneNumber,e.getMessage(),e);
            return SmsCodeResult.builder()
                    .sendSuccess(false)
                    .phoneNumber(phoneNumber)
                    .message("Fail send Sms code: " + e.getMessage())
                    .build();
        }
    }
}
