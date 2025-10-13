package org.waterwood.waterfunservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.AccountService;
import org.waterwood.waterfunservice.service.Impl.RSAJwtTokenService;
import org.waterwood.waterfunservice.service.authServices.AuthService;

@Slf4j
@Controller
@RequestMapping("/api/account")
public class AccountController {
    private final AuthService authService;
    private final AccountService accountService;
    private final RSAJwtTokenService tokenService;

    public AccountController(AuthService authService, AccountService accountService, RSAJwtTokenService tokenService) {
        this.authService = authService;
        this.accountService = accountService;
        this.tokenService = tokenService;
    }

    @PostMapping("get-user-info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        Long userId = tokenService.getCurrentUserId();
        if(userId == null) {
            return ResponseCode.USER_NOT_FOUND.toResponseEntity();
        }else {
            return accountService.getUserInfo(userId).toResponseEntity();
        }
    }
}
