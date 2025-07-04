package org.waterwood.waterfunservice.utils.streamApi;

import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.service.RedisServiceBase;
import org.waterwood.waterfunservice.utils.ValidateUtil;

import java.util.function.Supplier;

/**
 * A utility class for validating authentication-related data.
 * <p>It provides a fluent interface for chaining validation checks and returning results.</p>
 * <p>Will construct a result where the first condition is not met</p>
 */
public class AuthValidator {
    /**
     * Static method to start the validation process chain.
     * @return a new instance of AuthValidator.
     */
    public static AuthValidator start(){
        return new AuthValidator();
    }

    private ApiResponse<LoginServiceResponse> result;

    /**
     * Checks a condition and sets the result if the condition is false.
     * @param condition condition to check.
     * @param responseCode Error Code if the condition is false.
     * @return the current AuthValidator instance for method chaining.
     */
    public AuthValidator check(boolean condition, ResponseCode responseCode) {
        if(result == null){
            result = ApiResponse.failure(responseCode);
        }else{
            if(! result.isSuccess()){
                return this; // If already failed, skip further checks
            }
        }
        if(!condition){
            result = ApiResponse.failure(responseCode);
        }
        return this;
    }

    public AuthValidator ifValidThen(Supplier<ApiResponse<LoginServiceResponse>> supplier) {
        if(result != null && ! result.isSuccess()) {
            return this; // If already failed, skip further checks
        }
        ApiResponse<LoginServiceResponse> r = supplier.get();
        if (! r.isSuccess()) {
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
     * Validates the provided code against the saved ID in Redis using the specified service.
     * @param redisSavedID the ID under which the code is saved in Redis.
     * @param code the code to validate.
     * @param responseCode the error code to return if validation fails.
     * @return current AuthValidator instance for method chaining.
     */
    public AuthValidator validateCode(String redisSavedID, String code, RedisServiceBase<String> storeService, ResponseCode responseCode) {
        return check(storeService.validate(redisSavedID, code), responseCode);
    }

    /**
     * Returns the current AuthResult if it exists, otherwise returns the provided default result.
     * @param defaultResult the default AuthResult to return
     * @return current AuthResult if it exists, otherwise the default result.
     */
    public ApiResponse<LoginServiceResponse> orElse(ApiResponse<LoginServiceResponse> defaultResult) {
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

    public AuthValidator validateEmail(String email) {
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
    public ApiResponse<LoginServiceResponse> buildResult() {
        return result == null ? ResponseCode.BAD_REQUEST.toApiResponse() : result;
    }

    public ApiResponse<LoginServiceResponse> buildResult(boolean success) {
        if(result == null){
            if(success){
                result = ApiResponse.success();
            }else{
                result = ApiResponse.failure(ResponseCode.BAD_REQUEST);
            }
        }
        return result;
    }
}
