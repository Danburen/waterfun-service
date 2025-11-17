package org.waterwood.waterfunservice.service.auth;

import org.waterwood.waterfunservice.service.auth.impl.LoginServiceImpl;

public class AdminAuthService {
    private final LoginServiceImpl loginService;
    public AdminAuthService(LoginServiceImpl ls) {
        this.loginService = ls;
    }


}
