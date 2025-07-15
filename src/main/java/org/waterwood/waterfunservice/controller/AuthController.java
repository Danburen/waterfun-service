package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.converter.ApiConvertFactory;
import org.waterwood.waterfunservice.DTO.converter.LoginResponseConverter;
import org.waterwood.waterfunservice.DTO.request.*;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;
import org.waterwood.waterfunservice.service.dto.OpResult;
import org.waterwood.waterfunservice.service.dto.SmsCodeResult;
import org.waterwood.waterfunservice.service.authServices.RegisterService;
import org.waterwood.waterfunservice.service.authServices.*;
import org.waterwood.waterfunservice.utils.CookieParser;
import org.waterwood.waterfunservice.utils.CookieUtil;
import org.waterwood.waterfunservice.utils.ResponseUtil;

import java.io.IOException;
import java.time.Duration;

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

    @Autowired
    private AuthService authService;

    @PostMapping("refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request,HttpServletResponse response) {
        return authService.refreshAccessToken(
                CookieParser.getCookieValue(request.getCookies(),"REFRESH_TOKEN"))
                .toResponseEntity();
    }

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

    @GetMapping("/csrf-token")
    public ResponseEntity<?> getCsrfToken(HttpServletRequest request,HttpServletResponse response) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", token.getToken())
                .httpOnly(false)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-sms-code")
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

    @PostMapping("/send-email-code")
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
    public ResponseEntity<?> loginByPassword(@RequestBody PwdLoginRequestBody body, HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ApiResponse<LoginServiceResponse> apiResponse = loginService.loginByPassword(body,
                CookieParser.getCookieValue(cookies, "CAPTCHA_KEY"),
                CookieParser.getCookieValue(cookies,"ACCESS_TOKEN"),
                CookieParser.getCookieValue(cookies,"REFRESH_TOKEN"));
        return BuildLoginResponse(response, apiResponse);
    }

    @PostMapping("/login/sms")
    public ResponseEntity<?> loginBySms(@RequestBody SmsLoginRequestBody body, HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ApiResponse<LoginServiceResponse> apiResponse = loginService.loginBySmsCode(body,
                CookieParser.getCookieValue(cookies, "SMS_CODE_KEY"),
                CookieParser.getCookieValue(cookies,"ACCESS_TOKEN"),
                CookieParser.getCookieValue(cookies,"REFRESH_TOKEN"));
        return BuildLoginResponse(response, apiResponse);
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequestBody body, HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ApiResponse<LoginServiceResponse> apiResponse = loginService.loginByEmail(body,
                CookieParser.getCookieValue(cookies, "EMAIL_CODE_KEY"),
                CookieParser.getCookieValue(cookies,"ACCESS_TOKEN"),
                CookieParser.getCookieValue(cookies,"REFRESH_TOKEN"));
        return BuildLoginResponse(response, apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        ApiResponse<LoginServiceResponse> apiResponse = registerService.register(requestBody,
                CookieParser.getCookieValue(request.getCookies(), "SMS_CODE_KEY"));
        return BuildLoginResponse(response, apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        CookieUtil.clearTokenCookie(response);
        return loginService.logout(request).toResponseEntity();
    }


    private ResponseEntity<?> BuildLoginResponse(HttpServletResponse response, ApiResponse<LoginServiceResponse> apiResponse) {
        if(! apiResponse.isSuccess() || apiResponse.getData() == null) return apiResponse.toResponseEntity();
        CookieUtil.setTokenCookie(response,apiResponse.getData());
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        return  ResponseUtil.buildResponse(ApiConvertFactory.convert(
                apiResponse,loginResponseConverter
        ));
    }
}
