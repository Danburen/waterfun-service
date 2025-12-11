package org.waterwood.waterfunservice.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservicecore.api.req.EmailChangeDto;
import org.waterwood.waterfunservicecore.api.req.ResetPasswordDto;
import org.waterwood.waterfunservicecore.api.req.SetPasswordDto;
import org.waterwood.waterfunservicecore.api.req.EmailBindActivateDto;
import org.waterwood.waterfunservicecore.api.req.auth.SendCodeDto;
import org.waterwood.waterfunservicecore.api.resp.auth.CodeResult;
import org.waterwood.waterfunservicecore.infrastructure.utils.ResponseUtil;
import org.waterwood.waterfunservicecore.infrastructure.utils.context.UserContext;
import org.waterwood.waterfunservicecore.services.account.AccountService;
import org.waterwood.waterfunservicecore.services.auth.code.VerificationService;
import org.waterwood.waterfunservicecore.utils.CookieKeyGetter;

@RestController
@RequestMapping("/api/auth/account")
@RequiredArgsConstructor
public class AuthAccountController {
    private final AccountService accountService;
    private final VerificationService verificationService;

    @RequestMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto, HttpServletRequest request, @AuthenticationPrincipal UserContext user) {
        accountService.changePwd(user.getUserId(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), request.getCookies()),
                dto);
        return ApiResponse.success();
    }

    @RequestMapping("/password/set")
    public ApiResponse<Void> setPassword(@Valid @RequestBody SetPasswordDto dto, HttpServletRequest request,@AuthenticationPrincipal UserContext user) {
        accountService.setPassword(user.getUserId(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), request.getCookies()),
                dto);
        return ApiResponse.success();
    }


    @RequestMapping("/send-verify-code")
    public ApiResponse<Void> sendVerifyCode(@Valid @RequestBody SendCodeDto dto, HttpServletResponse response) {
        CodeResult result = verificationService.sendAuthorizedCode(dto);
        String cookieKey = dto.getChannel().name() + "_CODE_KEY";
        ResponseUtil.setCookieAndNoCache(response,cookieKey, result.getKey(), 120);
        return ApiResponse.success();
    }

    @RequestMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody EmailBindActivateDto dto, HttpServletRequest req, @AuthenticationPrincipal UserContext user){
        accountService.bindOrActivateEmail(user.getUserId(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto);
        return ApiResponse.success();
    }

    @RequestMapping("/email/change")
    public ApiResponse<Void> changeEmail(@Valid @RequestBody EmailChangeDto dto, HttpServletRequest req, @AuthenticationPrincipal UserContext user) {
        accountService.changeEmail(
                user.getUserId(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies())
                dto
        );
        return ApiResponse.success();
    }
}
