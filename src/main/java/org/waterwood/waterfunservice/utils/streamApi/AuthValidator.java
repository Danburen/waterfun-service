package org.waterwood.waterfunservice.utils.streamApi;

import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.AuthResult;
import org.waterwood.waterfunservice.service.RedisServiceBase;

import java.util.function.Supplier;
import java.util.stream.Stream;

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

    private AuthResult result = null;

    /**
     * Checks a condition and sets the result if the condition is false.
     * @param condition condition to check.
     * @param responseCode Error Code if the condition is false.
     * @return the current AuthValidator instance for method chaining.
     */
    public AuthValidator check(boolean condition, ResponseCode responseCode) {
        if(result == null || !condition){
            result = new AuthResult(false, responseCode);
        }
        return this;
    }

    public AuthValidator ifValidThen(Supplier<AuthResult> supplier) {
        AuthResult r = supplier.get();
        if (r.success()) {
            result = r;
        } else {
            result = new AuthResult(false, r.code());
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
     * Validates that the provided username is not null or empty.
     * @param username the username to validate.
     * @return the current AuthValidator instance for method chaining.
     */
    public AuthValidator validateUsername(String username) {
        return checkEmpty(username, ResponseCode.USERNAME_EMPTY);
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
    public AuthResult orElse(AuthResult defaultResult) {
        return result != null ? result : defaultResult;
    }

    /**
     * Builds the final AuthResult.
     * a new AuthResult indicating failure if no checks were performed.
     * @return result of the validation
     */
    public AuthResult buildResult() {
        return result == null ? new AuthResult(false,ResponseCode.BAD_REQUEST) : result;
    }
}
