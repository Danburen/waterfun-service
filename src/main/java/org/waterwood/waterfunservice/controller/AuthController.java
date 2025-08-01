package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
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
        ApiResponse<TokenResult> accessTokenRes = authService.refreshAccessToken(
                CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN"),
                dfp);
        TokenResult res = accessTokenRes.getData();
        if(res == null) return accessTokenRes.toResponseEntity();
        return ApiResponse.success(new LoginClientData(res.accessToken(),res.expire())).toResponseEntity();
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
        ApiResponse<SmsCodeResult> smsCodeResult = smsCodeService.sendSmsCode(requestBody.getPhoneNumber());
        if(! smsCodeResult.isSuccess()) {
            log.warn("Failed to send sms code to {} , {}",requestBody.getPhoneNumber(),smsCodeResult.getData().getResponseRaw());
            return ResponseCode.INTERNAL_SERVER_ERROR.toResponseEntity();
        }
        ResponseUtil.setCookieAndNoCache(response, "SMS_CODE_KEY", smsCodeResult.getData().getKey(), 120);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody SendEmailCodeRequest requestBody, HttpServletResponse response) {
        ApiResponse<EmailCodeResult> emailCodeResult = emailCodeService.sendEmailCode(requestBody.getEmail(), EmailTemplateType.VERIFY_CODE);
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
        ApiResponse<LoginServiceResponse> apiResponse = loginService.verifyPasswordLogin(body,
                CookieUtil.getCookieValue(cookies, "CAPTCHA_KEY"));
        log.info(apiResponse.getData().getUserId().toString());
        return BuildLoginResponse(response, apiResponse,body.getDeviceFp());
    }

    @PostMapping("/login/sms")
    public ResponseEntity<?> loginBySms(@RequestBody SmsLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ApiResponse<LoginServiceResponse> apiResponse = loginService.verifySmsCodeLogin(body,
                CookieUtil.getCookieValue(cookies, "SMS_CODE_KEY"));
        return BuildLoginResponse(response, apiResponse,body.getDeviceFp());
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginByEmail(@RequestBody EmailLoginRequestBody body, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ApiResponse<LoginServiceResponse> apiResponse = loginService.verifyEmailLogin(body,
                CookieUtil.getCookieValue(cookies, "EMAIL_CODE_KEY"));
        return BuildLoginResponse(response, apiResponse,body.getDeviceFp());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body, HttpServletRequest request, HttpServletResponse response) {
        ApiResponse<LoginServiceResponse> apiResponse = registerService.register(body,
                CookieUtil.getCookieValue(request.getCookies(), "SMS_CODE_KEY"));
        return BuildLoginResponse(response, apiResponse,body.getDeviceFp());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String dfp,HttpServletRequest request,HttpServletResponse response) {
        CookieUtil.clearTokenCookie(response);
        String refreshToken = CookieUtil.getCookieValue(request.getCookies(),"REFRESH_TOKEN");
        return loginService.logout(dfp,refreshToken).toResponseEntity();
    }


    private ResponseEntity<?> BuildLoginResponse(HttpServletResponse response, ApiResponse<LoginServiceResponse> apiResponse,String dfp) {
        if(! apiResponse.isSuccess() || apiResponse.getData() == null) return apiResponse.toResponseEntity();
        LoginServiceResponse data = apiResponse.getData();
        TokenPair tokenPair = authService.createNewTokens(data.getUserId(),dfp);
        CookieUtil.setTokenCookie(response,tokenPair);
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        return  ResponseUtil.buildResponse(ApiResponse.success(new LoginClientData(tokenPair.accessToken(),tokenPair.accessExp())));
    }
}
