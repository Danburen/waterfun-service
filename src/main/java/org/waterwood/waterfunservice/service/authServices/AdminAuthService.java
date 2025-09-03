package org.waterwood.waterfunservice.service.authServices;

import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;

public class AdminAuthService {
    private final LoginService loginService;
    public AdminAuthService(LoginService ls) {
        this.loginService = ls;
    }

    public ApiResponse<LoginServiceResponse> verifyAdminPwdLogin(PwdLoginRequestBody requestBody, String captchaUUID){
        ApiResponse<LoginServiceResponse> loginRes = loginService.verifyPasswordLogin(requestBody, captchaUUID);
        if(loginRes.isSuccess()){
            // TODO: ADD ADMIN ENHANCED AUTH LOGIC
            return loginRes;
        }
        return loginRes;
    }
}
