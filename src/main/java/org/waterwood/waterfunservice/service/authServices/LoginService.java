package org.waterwood.waterfunservice.service.authServices;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.repository.UserDatumRepo;
import org.waterwood.waterfunservice.service.EncryptedKeyService;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.service.TokenService;
import org.waterwood.waterfunservice.utils.CookieParser;
import org.waterwood.waterfunservice.utils.ValidateUtil;
import org.waterwood.waterfunservice.utils.security.HashUtil;
import org.waterwood.waterfunservice.utils.security.PartialEncryptionHelper;
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

@Service
public class LoginService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    AuthService authService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDatumRepo userDatumRepo;
    @Autowired
    private EncryptedKeyService encryptedKeyService;

    public ApiResponse<LoginServiceResponse> loginByPassword(PwdLoginRequestBody requestBody, String captchaUUID,String accessToken,String refreshToken) {
        return userRepo.findByUsername(requestBody.getUsername()).map(
                        user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                .checkEmpty(requestBody.getUsername(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                .checkEmpty(requestBody.getPassword(), ResponseCode.PASSWORD_EMPTY)
                                .checkEmpty(requestBody.getCaptcha(), ResponseCode.CAPTCHA_EMPTY)
                                .validateCode(captchaUUID, requestBody.getCaptcha(),authService.getCaptchaService(), ResponseCode.CAPTCHA_INCORRECT)
                                .ifValidThen(() -> {
                                    if (!user.checkPassword(requestBody.getPassword())) {
                                        return ApiResponse.failure(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
                                    }
                                    return ApiResponse.success();
                                }),accessToken, refreshToken, user))
                .orElseGet(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT::toApiResponse);
    }

    public  ApiResponse<LoginServiceResponse> loginBySmsCode(SmsLoginRequestBody requestBody, String uuid,String accessToken,String refreshToken) {
        String phone = requestBody.getPhoneNumber();
        String phonePrefix = PartialEncryptionHelper.getPhonePrefix(phone);
        if(!ValidateUtil.validatePhone(phone)) return ApiResponse.failure(ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID);
        return encryptedKeyService.pickEncryptionKey(1).map(key->
                userDatumRepo.findByPhonePrefixAndPhoneHash(phonePrefix, HashUtil.calculateHmac(phone,key.getEncryptedKey())).map(userDatum ->
                                userRepo.findById(userDatum.getId()).map(user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                                .checkEmpty(requestBody.getPhoneNumber(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                                .checkEmpty(requestBody.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                                                .check(authService.getSmsCodeService()
                                                        .verifySmsCode(phone, uuid, requestBody.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT)
                                        , accessToken, refreshToken, user))
                                .orElse(ResponseCode.USER_NOT_FOUND.toApiResponse()))
                        .orElse(ApiResponse.failure(ResponseCode.INTERNAL_SERVER_ERROR)))
                .orElseGet(ResponseCode.INTERNAL_SERVER_ERROR::toApiResponse);
    }

    public  ApiResponse<LoginServiceResponse> loginByEmail(EmailLoginRequestBody requestBody, String uuid,String accessToken,String refreshToken) {
        String email = requestBody.getEmail();
        String emailDisplay = PartialEncryptionHelper.getEmailDisplay(email);
        if(!ValidateUtil.validateEmail(email)) return ApiResponse.failure(ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID);
        return encryptedKeyService.pickEncryptionKey(1).map(key->
                userDatumRepo.findByEmailDisplayAndEmailHash(emailDisplay,HashUtil.calculateHmac(email,key.getEncryptedKey())).map(userDatum ->
                                userRepo.findById(userDatum.getId()).map(user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                                        .checkEmpty(requestBody.getEmail(),ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID)
                                                        .checkEmpty(requestBody.getEmailCode(), ResponseCode.EMAIL_CODE_EMPTY)
                                                        .check(authService.getEmailCodeService().verifyEmailCode(
                                                                requestBody.getEmail(),uuid,requestBody.getEmailCode()), ResponseCode.EMAIL_CODE_INCORRECT)
                                                ,accessToken, refreshToken, user))
                                        .orElse(ResponseCode.USER_NOT_FOUND.toApiResponse()))
                        .orElseGet(ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID::toApiResponse))
                .orElseGet(ResponseCode.INTERNAL_SERVER_ERROR::toApiResponse);
    }

    public ResponseCode logout(HttpServletRequest request) {
        String refreshToken = CookieParser.getCookieValue(request.getCookies(),"REFRESH_TOKEN");
        tokenService.removeRefreshToken(refreshToken);
        return ResponseCode.OK;
    }
}
