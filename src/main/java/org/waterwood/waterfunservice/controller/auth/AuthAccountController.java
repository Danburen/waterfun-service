package org.waterwood.waterfunservice.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservice.api.request.PhoneChangeActivateDto;
import org.waterwood.waterfunservicecore.api.VerifyChannel;
import org.waterwood.waterfunservicecore.api.resp.AccountResp;
import org.waterwood.waterfunservice.api.request.EmailChangeDto;
import org.waterwood.waterfunservice.api.request.ResetPasswordDto;
import org.waterwood.waterfunservice.api.request.SetPasswordDto;
import org.waterwood.waterfunservice.api.request.EmailBindActivateDto;
import org.waterwood.waterfunservicecore.api.resp.auth.CodeResult;
import org.waterwood.waterfunservicecore.infrastructure.utils.ResponseUtil;
import org.waterwood.waterfunservicecore.infrastructure.utils.context.UserContext;
import org.waterwood.waterfunservice.service.account.AccountService;
import org.waterwood.waterfunservicecore.services.auth.code.VerificationService;
import org.waterwood.waterfunservicecore.services.user.UserDatumCoreService;
import org.waterwood.waterfunservicecore.utils.CookieKeyGetter;

@Slf4j
@RestController
@RequestMapping("/api/auth/account")
@RequiredArgsConstructor
public class AuthAccountController {
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final UserDatumCoreService userDatumCoreService;

    @GetMapping
    public ApiResponse<AccountResp> get(@Parameter(hidden = true) @AuthenticationPrincipal UserContext user){
        return ApiResponse.success(
            userDatumCoreService.getAccountInfo(user.getUserUid())
        );
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto, HttpServletRequest request,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        accountService.changePwd(user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), request.getCookies()),
                dto);
        return ApiResponse.success();
    }

    @Operation(summary = "设置密码")
    @PostMapping("/password/set")
    public ApiResponse<Void> setPassword(@Valid @RequestBody SetPasswordDto dto, HttpServletRequest request,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        accountService.setPassword(user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), request.getCookies()),
                dto);
        return ApiResponse.success();
    }
    @Operation(summary = "绑定邮箱")
    @PostMapping("/email/bind")
    public ApiResponse<Void> bindEmail(@Valid @RequestBody EmailBindActivateDto dto, HttpServletRequest req, HttpServletResponse res,
                                       @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        CodeResult result =  accountService.bindEmail(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto
        );
        String cookieKey = "EMAIL_CODE_KEY";
        ResponseUtil.setCookieAndNoCache(res, cookieKey, result.getKey(), 120);
        return ApiResponse.success();
    }
    @Operation(summary = "激活邮箱")
    @PostMapping("/email/activate")
    public ApiResponse<Void> activateEmail(@Valid @RequestBody EmailBindActivateDto dto, HttpServletRequest req,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserContext user){
        accountService.activateEmail(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto
        );
        return ApiResponse.success();
    }


    @Operation(summary = "修改邮箱")
    @PostMapping("/email/change")
    public ApiResponse<Void> changeEmail(@Valid @RequestBody EmailChangeDto dto, HttpServletRequest req, HttpServletResponse res,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        VerifyChannel channel = dto.getVerify().getChannel();
        CodeResult result = accountService.changeEmail(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(channel, req.getCookies()),
                dto
        );
        String cookieKey = channel.name() + "_CODE_KEY";
        ResponseUtil.setCookieAndNoCache(res, cookieKey, result.getKey(), 120);
        return ApiResponse.success();
    }

    @Operation(summary = "修改手机号")
    @PostMapping("/phone/change")
    public ApiResponse<Void> changePhone(@Valid @RequestBody PhoneChangeActivateDto dto, HttpServletRequest req, HttpServletResponse res,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        CodeResult result = accountService.changePhone(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto
        );
        String cookieKey = "SMS_CODE_KEY";
        ResponseUtil.setCookieAndNoCache(res, cookieKey, result.getKey(), 120);
        return ApiResponse.success();
    }

    @Operation(summary = "激活手机号")
    @PostMapping("/phone/activate")
    public ApiResponse<Void> activatePhone(@Valid @RequestBody PhoneChangeActivateDto dto, HttpServletRequest req,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        accountService.activatePhone(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto
        );
        return ApiResponse.success();
    }

    @Operation(summary = "解绑邮箱")
    @PostMapping("/email/unbind")
    public ApiResponse<Void> unbindEmail(@Valid @RequestBody EmailBindActivateDto dto, HttpServletRequest req,
                                         HttpServletResponse res,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserContext user) {
        accountService.unbindEmail(
                user.getUserUid(),
                CookieKeyGetter.getChannelVerifyCodeKey(dto.getVerify().getChannel(), req.getCookies()),
                dto
        );
        return ApiResponse.success();
    }
}
