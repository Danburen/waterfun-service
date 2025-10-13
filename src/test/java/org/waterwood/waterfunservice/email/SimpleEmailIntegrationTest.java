package org.waterwood.waterfunservice.email;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.service.Impl.SpringEmailService;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;

import java.util.Map;
@SpringBootTest
public class SimpleEmailIntegrationTest {
    @Autowired
    private SpringEmailService emailService;

    @Test
    public void testSendEmailActuallyWorks() {
        // 直接调用真实方法（注意：这会真的尝试发送邮件）
        EmailCodeResult result = emailService.sendHtmlEmail(
                "test@example.com",  // 替换为你的测试邮箱
                EmailTemplateType.VERIFY_CODE,
                Map.of("code", "123456")
        );

        // 简单验证是否返回成功
        assertTrue(result.isSendSuccess());
        System.out.println("邮件发送结果: " + result);
    }

    @Test
    public void testSendSimpleEmail() {
        EmailCodeResult result = emailService.sendSimpleEmail(
                "2964361672@qq.com",
                "2964361672@qq.com",
                "Main",
                "Hello"
        );
        assertTrue(result.isSendSuccess());
        System.out.println("邮件发送结果: " + result);
    }
}
