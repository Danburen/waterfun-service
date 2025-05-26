package org.waterwood.waterfunservice.service.authServices;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Interface that define a class whether is to handle
 * verify code
 */
public interface VerifyCodeService {
    /**
     * Default Save verify code to repository
     * Code will be expired in <b>2 minutes.</b>
     * @param uuid uuid of the code
     * @param code string code
     */
    void saveCode(String uuid,String code);
    /**
     * Save verify code to repository
     * expired use <b>minutes</b>>
     * @param uuid uuid of the code
     * @param code string code
     * @param expireMinutes minutes to expired
     */
    void saveCode(String uuid,String code, long expireMinutes);
    /**
     * Save verify code to repository
     * @param uuid uuid of the code
     * @param code string code
     * @param expire expiration of coden
     */
    void saveCode(String uuid, String code, Duration expire);
    /**
     * Get verify code from repository
     * @param uuid uuid of code
     * @return the verify code
     */
    String getCode(String uuid);
    /**
     * Remove and destroy the code from repository
     * @param uuid uuid of code
     */
    void removeCode(String uuid);
    /**
     * Generate the verify code
     * @return verify code
     */
    Object generateVerifyCode();
}
