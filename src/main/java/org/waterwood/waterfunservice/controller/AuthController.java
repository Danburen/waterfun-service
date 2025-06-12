package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.DTO.common.ErrorCode;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.LoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.service.authServices.CaptchaService;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CaptchaService captchaService;

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
            return ErrorCode.USERNAME_EMPTY.toResponseEntity();
        }

        if(loginRequestBody instanceof PwdLoginRequestBody body){
            if(body.getPassword() == null || body.getPassword().isEmpty()) {
                return ErrorCode.PASSWORD_EMPTY.toResponseEntity();
            }
            String uuid = Arrays.stream(request.getCookies())
                .filter(c->"CAPTCHA_KEY".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
            if (uuid == null) {
                return ErrorCode.CAPTCHA_EXPIRED.toResponseEntity();
            }
            if(!captchaService.validate(uuid, body.getCaptcha())){
                return ErrorCode.CAPTCHA_INCORRECT.toResponseEntity();
            }
            if (!"admin".equals(body.getUsername()) || !"123456".equals(body.getPassword())) {
                return ErrorCode.USERNAME_OR_PASSWORD_INCORRECT.toResponseEntity();
            }

        }else if(loginRequestBody instanceof SmsLoginRequestBody body){

        } else if (loginRequestBody instanceof EmailLoginRequestBody body) {

        }
        return ResponseEntity.ok("Successfully Login!");
    }
}
