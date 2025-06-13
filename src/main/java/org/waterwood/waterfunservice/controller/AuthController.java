package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.LoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.service.SmsService;
import org.waterwood.waterfunservice.service.authServices.AuthService;
import org.waterwood.waterfunservice.service.authServices.CaptchaService;
import org.waterwood.waterfunservice.utils.CookieParser;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;

    @Autowired
    private AuthService authService;
    /** redis + cookie(HttpOnly) save captcha
     * Generate the captcha
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response) throws IOException{
        CaptchaService.LineCaptchaResult result = captchaService.generateCaptcha();
        Cookie cookie = new Cookie("CAPTCHA_KEY",result.uuid());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(120);
        response.addCookie(cookie);
        // set the header of response
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setDateHeader("Expires", 0);
        // write img stream to response stream
        result.captcha().write(response.getOutputStream());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestBody loginRequestBody, HttpServletRequest request) {
        if(loginRequestBody.getUsername() == null || loginRequestBody.getUsername().isEmpty()) {
            return ResponseCode.USERNAME_EMPTY.toResponseEntity();
        }

        if(loginRequestBody instanceof PwdLoginRequestBody body){
            return authService.loginByPassword(body,
                    CookieParser.getCookieValue(request.getCookies(),"CAPTCHA_KEY")).toResponseEntity();
        }else if(loginRequestBody instanceof SmsLoginRequestBody body){
            return authService.loginBySmsCode(body,
                    CookieParser.getCookieValue(request.getCookies(),"SMS_CODE_KEY")).toResponseEntity();
        } else if (loginRequestBody instanceof EmailLoginRequestBody body) {
            return authService.loginByEmail(body,
                    CookieParser.getCookieValue(request.getCookies(),"EMAIL_CODE_KEY")).toResponseEntity();
        }

        return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
    }
}
