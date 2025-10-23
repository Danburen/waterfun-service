package org.waterwood.waterfunservice.service.authServices;

import lombok.extern.slf4j.Slf4j;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.utils.ValidateUtil;

import java.util.function.Supplier;

/**
 * A utility class for validating authentication-related data.
 * <p>It provides a fluent interface for chaining validation checks and returning results.</p>
 * <p>Will construct a result where the first condition is not met</p>
 */
@Slf4j
public class AuthValidator {
    /**
     * Static method to start the validation process chain.
     * @return a new instance of AuthValidator.
     */
    public static AuthValidator start(){
        AuthValidator validator = new AuthValidator();
        validator.result = ServiceResult.success();
        return validator;
    }

    private ServiceResult<LoginServiceResponse> result;

    /**
     * Checks a condition and sets the result if the condition is false.
     * @param condition condition to check.
     * @param responseCode Error Code if the condition is false.
     * @return the current AuthValidator instance for method chaining.
     */
    public AuthValidator check(boolean condition, ResponseCode responseCode) {
        if(result == null){
            result = ServiceResult.success();
        }else{
            if(! result.isSuccess()){
                return this; // If already failed, skip further checks
            }
        }
        if(!condition){
            result = ServiceResult.failure(responseCode);
        }
        return this;
    }

    public AuthValidator then(Supplier<ServiceResult<LoginServiceResponse>> supplier) {
        if(result != null && ! result.isSuccess()) {
            return this; // If already failed, skip further checks
        }
        ServiceResult<LoginServiceResponse> r = supplier.get();
        log.info(r.getData().toString());
        if (r.isSuccess()) {
            result = r;
        }
        return this;
    }
    /**
     * Checks if the provided value is not null or empty.
     * @param value the value to check.
     * @param responseCode Error Code if the value is null or empty.
     * @return current AuthValidator instance for method chaining.
     */
    public AuthValidator checkEmpty(String value, ResponseCode responseCode) {
        return check(value != null && !value.isEmpty(), responseCode);
    }

    /**
     * Returns the current AuthResult if it exists, otherwise returns the provided default result.
     * @param defaultResult the default AuthResult to return
     * @return current AuthResult if it exists, otherwise the default result.
     */
    public ServiceResult<LoginServiceResponse> orElse(ServiceResult<LoginServiceResponse> defaultResult) {
        return result != null ? result : defaultResult;
    }

    /**
     * Validates that the provided username is not null or empty.
     * @param username the username to validate.
     * @return the current AuthValidator instance for method chaining.
     */
    public AuthValidator validateUsername(String username) {
        return check(ValidateUtil.validateUsername(username), ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
    }

    public AuthValidator validateEmail(String email,boolean allowEmpty) {
        return check(ValidateUtil.validateEmail(email), ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID);
    }

    public AuthValidator validatePhone(String phone) {
        return check(ValidateUtil.validatePhone(phone), ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID);
    }

    /**
     * Builds the final AuthResult.
     * a new AuthResult indicating failure if no checks were performed.
     * @return result of the validation
     */
    public ServiceResult<LoginServiceResponse> buildResult() {
        return result == null ? ResponseCode.BAD_REQUEST.toServiceResult() : result;
    }

    public ServiceResult<LoginServiceResponse> buildResult(boolean success) {
        if(result == null){
            if(success){
                result = ServiceResult.success();
            }else{
                result = ServiceResult.failure(ResponseCode.BAD_REQUEST);
            }
        }
        return result;
    }
}
