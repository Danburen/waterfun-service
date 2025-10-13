package org.waterwood.waterfunservice.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.service.Impl.SpringEmailService;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class SimpleEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private SpringEmailService emailService;

    @Test
    void testSendSimpleEmail() {
        // 直接调用方法
        EmailCodeResult result = emailService.sendSimpleEmail(
                "1528749286@qq.com",
                "2964361672@qq.com",
                "Test Subject",
                "Test content"
        );

        // 简单验证是否返回成功
        assertTrue(result.isSendSuccess());
    }

    @Test
    void testSendHtmlEmailWithTemplate() {
        // 模拟模板引擎返回固定内容
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Test</html>");

        // 直接调用方法
        EmailCodeResult result = emailService.sendHtmlEmail(
                "test@example.com",
                EmailTemplateType.VERIFY_CODE,
                Map.of("key", "value")
        );

        // 简单验证是否返回成功
        assertTrue(result.isSendSuccess());
    }
}
