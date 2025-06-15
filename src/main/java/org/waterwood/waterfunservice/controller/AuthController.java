package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.EmailCodeResult;
import org.waterwood.waterfunservice.DTO.common.result.OperationResult;
import org.waterwood.waterfunservice.DTO.common.result.SmsCodeResult;
import org.waterwood.waterfunservice.DTO.request.*;
import org.waterwood.waterfunservice.service.authServices.RegisterService;
import org.waterwood.waterfunservice.service.authServices.*;
import org.waterwood.waterfunservice.utils.CookieParser;
import org.waterwood.waterfunservice.utils.ResponseUtil;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private EmailCodeService emailCodeService;
    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private LoginService loginService;
    @Autowired
    private RegisterService registerService;
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

    @PostMapping("/sendSmsCode")
    public ResponseEntity<?> sendSmsCode(@RequestBody SendSmsCodeRequest requestBody,HttpServletRequest request, HttpServletResponse response) {
        OperationResult<SmsCodeResult> smsCodeResult = smsCodeService.sendSmsCode(requestBody.getPhoneNumber());
        ResponseCode statusCode = smsCodeResult.getResponseCode();
        if(! smsCodeResult.isTrySuccess()){
            if(statusCode == null){
                return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
            }else{
                return statusCode.toResponseEntity();
            }
        }
        ResponseUtil.setCookieAndNoCache(response, "SMS_CODE_KEY", smsCodeResult.getResultData().getKey(), 120);
        return ResponseCode.OK.toResponseEntity();
    }

    @PostMapping("/sendEmailCode")
    public ResponseEntity<?> sendEmailCode(@RequestBody SendEmailCodeRequest requestBody,HttpServletRequest request, HttpServletResponse response) {
        OperationResult<EmailCodeResult> emailCodeResult = emailCodeService.sendEmailCode(requestBody.getEmail(), EmailTemplateType.VERIFY_CODE);
        ResponseCode statusCode = emailCodeResult.getResponseCode();
        if(! emailCodeResult.isTrySuccess()){
            if(statusCode == null){
                return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
            }else{
                return statusCode.toResponseEntity();
            }
        }
        ResponseUtil.setCookieAndNoCache(response, "EMAIL_CODE_KEY", emailCodeResult.getResultData().getKey(), 120);
        return ResponseCode.OK.toResponseEntity();
    }

    @PostMapping("/login/password")
    public ResponseEntity<?> loginByPassword(@RequestBody PwdLoginRequestBody body, HttpServletRequest request) {
        return loginService.loginByPassword(
                body,
                CookieParser.getCookieValue(request.getCookies(), "CAPTCHA_KEY")
        ).toResponseEntity();
    }

    @PostMapping("/login/sms")
    public ResponseEntity<?> loginBySms(@RequestBody SmsLoginRequestBody body, HttpServletRequest request) {
        return loginService.loginBySmsCode(
                body,
                CookieParser.getCookieValue(request.getCookies(), "SMS_CODE_KEY")
        ).toResponseEntity();
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequestBody body, HttpServletRequest request) {
        return loginService.loginByEmail(
                body,
                CookieParser.getCookieValue(request.getCookies(), "EMAIL_CODE_KEY")
        ).toResponseEntity();
    }
}
