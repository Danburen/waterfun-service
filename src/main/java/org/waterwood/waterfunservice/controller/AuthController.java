package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.request.*;
import org.waterwood.waterfunservice.DTO.response.LoginClientData;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.service.common.TokenPair;
import org.waterwood.waterfunservice.service.authServices.RegisterService;
import org.waterwood.waterfunservice.service.authServices.*;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.service.dto.SmsCodeResult;
import org.waterwood.waterfunservice.utils.CookieUtil;
import org.waterwood.waterfunservice.utils.ResponseUtil;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CaptchaService captchaService;
    private final EmailCodeService emailCodeService;
    private final SmsCodeService smsCodeService;
    private final LoginService loginService;
    private final RegisterService registerService;
    private final AuthService authService;

    public AuthController(CaptchaService cs, EmailCodeService emcs, SmsCodeService smcs, LoginService ls, RegisterService rs, AuthService as) {
        this.captchaService = cs;
        this.emailCodeService = emcs;
        this.smsCodeService = smcs;
        this.loginService = ls;
        this.registerService = rs;
        this.authService = as;
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String dfp, HttpServletRequest request,HttpServletResponse response) {
        ServiceResult<TokenResult> accessTokenRes = authService.refreshAccessToken(
                CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN"),
                dfp);
        TokenResult res = accessTokenRes.getData();
        if(res == null) return accessTokenRes.toResponseEntity();
        return ServiceResult.success(new LoginClientData(res.tokenValue(),res.expire())).toResponseEntity();
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
        response.setHeader("Set-cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-sms-code")
    public ResponseEntity<?> sendSmsCode(@RequestBody SendSmsCodeRequest requestBody, HttpServletResponse response) {
        ServiceResult<SmsCodeResult> smsCodeResult = smsCodeService.sendSmsCode(requestBody.getPhoneNumber());
        if(! smsCodeResult.isSuccess()) {
            log.warn("Failed to send sms code to {} , {}",requestBody.getPhoneNumber(),smsCodeResult.getData().getResponseRaw());
            return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
        }
        ResponseUtil.setCookieAndNoCache(response, "SMS_CODE_KEY", smsCodeResult.getData().getKey(), 120);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody SendEmailCodeRequest requestBody, HttpServletResponse response) {
        ServiceResult<EmailCodeResult> emailCodeResult = emailCodeService.sendEmailCode(requestBody.getEmail(), EmailTemplateType.VERIFY_CODE);
        if(! emailCodeResult.getData().isSendSuccess()) {
            log.warn("Failed to send email code to {},{}",requestBody.getEmail(),emailCodeResult.getData().getResponseRaw());
            return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
        }
        ResponseUtil.setCookieAndNoCache(response, "EMAIL_CODE_KEY", emailCodeResult.getData().getKey(), 120);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/password")
    public ResponseEntity<?> loginByPassword(@RequestBody PwdLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ServiceResult<LoginServiceResponse> ServiceResult = loginService.verifyPasswordLogin(body,
                CookieUtil.getCookieValue(cookies, "CAPTCHA_KEY"));
        log.info(ServiceResult.getData().getUserId().toString());
        return BuildLoginResponse(response, ServiceResult,body.getDeviceFp());
    }

    @PostMapping("/admin/login/password")
    public ResponseEntity<?> adminLoginByPassword(@RequestBody PwdLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ServiceResult<LoginServiceResponse> ServiceResult = loginService.verifyPasswordLogin(body,
                CookieUtil.getCookieValue(cookies, "CAPTCHA_KEY"));
        log.info(ServiceResult.getData().getUserId().toString());
        return BuildLoginResponse(response, ServiceResult,body.getDeviceFp());
    }

    @PostMapping("/login/sms")
    public ResponseEntity<?> loginBySms(@RequestBody SmsLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ServiceResult<LoginServiceResponse> ServiceResult = loginService.verifySmsCodeLogin(body,
                CookieUtil.getCookieValue(cookies, "SMS_CODE_KEY"));
        return BuildLoginResponse(response, ServiceResult,body.getDeviceFp());
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ServiceResult<LoginServiceResponse> ServiceResult = loginService.verifyEmailLogin(body,
                CookieUtil.getCookieValue(cookies, "EMAIL_CODE_KEY"));
        return BuildLoginResponse(response, ServiceResult,body.getDeviceFp());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body, HttpServletRequest request, HttpServletResponse response) {
        ServiceResult<LoginServiceResponse> ServiceResult = registerService.register(body,
                CookieUtil.getCookieValue(request.getCookies(), "SMS_CODE_KEY"));
        return BuildLoginResponse(response, ServiceResult,body.getDeviceFp());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String deviceFp,HttpServletRequest request,HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN");
        ServiceResult<Void> result = loginService.logout(refreshToken, deviceFp);
        if(result.isSuccess()) CookieUtil.cleanTokenCookie(response);
        return result.toApiResponse().toResponseEntity();
    }


    private ResponseEntity<?> BuildLoginResponse(HttpServletResponse response, ServiceResult<LoginServiceResponse> ServiceResult,String dfp) {
        if(! ServiceResult.isSuccess() || ServiceResult.getData() == null) return ServiceResult.toApiResponse().toResponseEntity();
        LoginServiceResponse data = ServiceResult.getData();
        TokenPair tokenPair = authService.createNewTokens(data.getUserId(),dfp);
        CookieUtil.setTokenCookie(response,tokenPair);
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        return  ResponseUtil.buildResponse(ServiceResult.success(new LoginClientData(tokenPair.accessToken(),tokenPair.accessExp())));
    }
}
