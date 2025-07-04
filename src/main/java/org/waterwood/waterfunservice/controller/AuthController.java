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
import org.waterwood.waterfunservice.DTO.converter.ApiConvertFactory;
import org.waterwood.waterfunservice.DTO.converter.LoginResponseConverter;
import org.waterwood.waterfunservice.DTO.request.*;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.response.LoginClientResponse;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;
import org.waterwood.waterfunservice.service.dto.OpResult;
import org.waterwood.waterfunservice.service.dto.SmsCodeResult;
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

    @Autowired
    LoginResponseConverter loginResponseConverter;
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
    public ResponseEntity<?> sendSmsCode(@RequestBody SendSmsCodeRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        OpResult<SmsCodeResult> smsCodeResult = smsCodeService.sendSmsCode(requestBody.getPhoneNumber());
        ResponseCode statusCode = smsCodeResult.getResponseCode();
        if(! smsCodeResult.isTrySuccess()){
            if(statusCode == null){
                return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
            }else{
                return statusCode.toResponseEntity();
            }
        }
        if (statusCode != null) {
            log.info(smsCodeResult.getServiceErrorCode() != null ?
                    "Error: " + statusCode + ", Service Error Code: " + smsCodeResult.getServiceErrorCode() :
                    "Error: " + statusCode);
            log.info(smsCodeResult.getResultData().getResponseRaw());
        }else{
            log.info("Null smsCode.");
        }
        ResponseUtil.setCookieAndNoCache(response, "SMS_CODE_KEY", smsCodeResult.getResultData().getKey(), 120);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sendEmailCode")
    public ResponseEntity<?> sendEmailCode(@RequestBody SendEmailCodeRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        OpResult<EmailCodeResult> emailCodeResult = emailCodeService.sendEmailCode(requestBody.getEmail(), EmailTemplateType.VERIFY_CODE);
        ResponseCode statusCode = emailCodeResult.getResponseCode();
        if(! emailCodeResult.isTrySuccess()){
            if(statusCode == null){
                return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
            }else{
                return statusCode.toResponseEntity();
            }
        }
        ResponseUtil.setCookieAndNoCache(response, "EMAIL_CODE_KEY", emailCodeResult.getResultData().getKey(), 120);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/password")
    public ResponseEntity<?> loginByPassword(@RequestBody PwdLoginRequestBody body, HttpServletRequest request) {
        return ResponseUtil.buildResponse(toClientResponse(
                loginService.loginByPassword(body, CookieParser.getCookieValue(request.getCookies(), "CAPTCHA_KEY"))));
    }

    @PostMapping("/login/sms")
    public ResponseEntity<?> loginBySms(@RequestBody SmsLoginRequestBody body, HttpServletRequest request) {
        return  ResponseUtil.buildResponse(toClientResponse(
                loginService.loginBySmsCode(body, CookieParser.getCookieValue(request.getCookies(), "SMS_CODE_KEY"))
        ));
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequestBody body, HttpServletRequest request) {
        return  ResponseUtil.buildResponse(toClientResponse(
                loginService.loginByEmail(body, CookieParser.getCookieValue(request.getCookies(), "EMAIL_CODE_KEY"))
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        ApiResponse<LoginServiceResponse> apiResponse = registerService.register(requestBody,
                CookieParser.getCookieValue(request.getCookies(), "SMS_CODE_KEY"));
        ResponseUtil.setTokenCookie(response,apiResponse.getData());
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        return ResponseUtil.buildResponse(toClientResponse(apiResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        return loginService.logout(request).toResponseEntity();
    }

    private ApiResponse<LoginClientResponse> toClientResponse(ApiResponse<LoginServiceResponse> source){
        return ApiConvertFactory.convert(
                source,loginResponseConverter
        );
    }
}
