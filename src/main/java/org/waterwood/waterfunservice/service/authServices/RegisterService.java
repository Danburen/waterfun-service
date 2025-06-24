package org.waterwood.waterfunservice.service.authServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.response.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponseData;
import org.waterwood.waterfunservice.DTO.common.result.AuthResult;
import org.waterwood.waterfunservice.DTO.request.RegisterRequest;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.PasswordUtil;
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

@Service
public class RegisterService {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepo;
    public ApiResponse<LoginResponseData> register(RegisterRequest request,String uuid) {
        AuthResult result = AuthValidator.start()
                .validateUsername(request.getUsername())
                .checkEmpty(request.getPassword(), ResponseCode.PASSWORD_EMPTY)
                .checkEmpty(request.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                .check(authService.getSmsCodeService().verifySmsCode(
                        request.getPhoneNumber(),uuid,request.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT)
                .buildResult();
        if(! result.success()) return result.toLoginResponse();
        return userRepo.findByUsername(request.getUsername()).map(
                        user -> new AuthResult(false, ResponseCode.USER_ALREADY_EXISTS).toLoginResponse())
                .orElseGet(() -> {
                    // Create a new user
                    User user = userRepo.save(new User(request.getUsername(), PasswordUtil.encryptPassword(request.getPassword())));
                    // Create a new user datum
                    Long userId = user.getId();

                    return authService.validateTokenAndBuildResult(AuthValidator.start().buildResult(),
                            null, null, user).t;
                });
    }
}
