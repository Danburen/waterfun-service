package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.api.TokenPair;
import org.waterwood.common.TokenResult;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservicecore.services.auth.*;
import org.waterwood.api.enums.EmailTemplateType;
import org.waterwood.waterfunservicecore.api.req.auth.*;
import org.waterwood.waterfunservicecore.api.resp.auth.LoginClientData;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.services.email.EmailCodeResult;
import org.waterwood.waterfunservicecore.api.resp.auth.SmsCodeResult;
import org.waterwood.waterfunservicecore.infrastructure.utils.CookieUtil;
import org.waterwood.waterfunservicecore.infrastructure.utils.ResponseUtil;
import org.waterwood.waterfunservicecore.services.email.EmailCodeService;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {
    private final CaptchaServiceImpl captchaService;
    private final EmailCodeService emailCodeService;
    private final SmsCodeService smsCodeService;
    private final LoginServiceImpl loginService;
    private final RegisterServiceImpl registerService;
    private final AuthServiceImpl authService;

    public AuthController(CaptchaServiceImpl cs, EmailCodeService emcs, SmsCodeService smcs, LoginServiceImpl ls, RegisterServiceImpl rs, AuthServiceImpl as) {
        this.captchaService = cs;
        this.emailCodeService = emcs;
        this.smsCodeService = smcs;
        this.loginService = ls;
        this.registerService = rs;
        this.authService = as;
    }

    @PostMapping("/refresh-access-token")
    public ApiResponse<LoginClientData> refreshAccessToken(@Valid @NotBlank(message = "{auth.device_fingerprint.required}") String dfp, HttpServletRequest request, HttpServletResponse response) {
        TokenResult res = authService.refreshAccessToken(
                CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN"),
                dfp);
        LoginClientData data = new LoginClientData(res.tokenValue(),res.expire());
        return ApiResponse.success(data);
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response) throws IOException{
        LineCaptchaResult result = captchaService.generateCaptcha();
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
    public ApiResponse<Void> getCsrfToken(HttpServletRequest request,HttpServletResponse response) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(token == null){
            return ApiResponse.success();
        }
        ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", token.getToken())
                .httpOnly(false)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();
        response.setHeader("Set-cookie", cookie.toString());
        return ApiResponse.success();
    }

    @PostMapping("/send-sms-code")
    public ApiResponse<Void> sendSmsCode(@Valid @RequestBody SendSmsCodeRequest requestBody, HttpServletResponse response) {
        SmsCodeResult result = smsCodeService.sendSmsCode(requestBody.getPhoneNumber());
        ResponseUtil.setCookieAndNoCache(response, "SMS_CODE_KEY", result.getKey(), 120);
        return ApiResponse.success();
    }

    @PostMapping("/send-email-code")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody SendEmailCodeRequest requestBody, HttpServletResponse response) {
        EmailCodeResult result = emailCodeService.sendEmailCode(requestBody.getEmail(), EmailTemplateType.VERIFY_CODE);
        ResponseUtil.setCookieAndNoCache(response, "EMAIL_CODE_KEY", result.getKey(), 120);
        return ApiResponse.success();
    }

    @PostMapping("/login/password")
    public ApiResponse<LoginClientData> loginByPassword(@Valid @RequestBody PwdLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        User user = loginService.login(body, CookieUtil.getCookieValue(cookies, "CAPTCHA_KEY"));
        return BuildLoginResponse(response, user,body.getDeviceFp());
    }

    @PostMapping("/admin/login/password")
    public ApiResponse<LoginClientData> adminLoginByPassword(@Valid @RequestBody PwdLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        User user =  loginService.login(body, CookieUtil.getCookieValue(request, "CAPTCHA_KEY"));
        return BuildLoginResponse(response, user,body.getDeviceFp());
    }

    @PostMapping("/login/sms")
    public ApiResponse<LoginClientData> loginBySms(@Valid @RequestBody SmsLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        User user = loginService.login(body, CookieUtil.getCookieValue(request, "SMS_CODE_KEY"));
        return BuildLoginResponse(response, user,body.getDeviceFp());
    }

    @PostMapping("/login/email")
    public ApiResponse<LoginClientData> loginByEmail(@Valid @RequestBody EmailLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        User user = loginService.login(body, CookieUtil.getCookieValue(request, "EMAIL_CODE_KEY"));
        return BuildLoginResponse(response, user,body.getDeviceFp());
    }

    @PostMapping("/register")
    public ApiResponse<LoginClientData> register(@Valid @RequestBody RegisterRequest body, HttpServletRequest request, HttpServletResponse response) {
        User user = registerService.register(body,
                CookieUtil.getCookieValue(request.getCookies(), "SMS_CODE_KEY"));
        return BuildLoginResponse(response, user,body.getDeviceFp());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody String deviceFp,HttpServletRequest request,HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN");
        boolean  result = loginService.logout(refreshToken, deviceFp);
        if(result) CookieUtil.cleanTokenCookie(response);
        return ApiResponse.success();
    }


    private ApiResponse<LoginClientData> BuildLoginResponse(HttpServletResponse response, User user,String dfp) {
        TokenPair tokenPair = authService.createNewTokens(user.getId(), dfp);
        CookieUtil.setTokenCookie(response,tokenPair);
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        return  ApiResponse.success(new LoginClientData(tokenPair.accessToken(),tokenPair.accessExp()));
    }
}
