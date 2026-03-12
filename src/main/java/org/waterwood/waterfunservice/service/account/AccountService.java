package org.waterwood.waterfunservice.service.account;


import org.waterwood.waterfunservice.api.request.*;
import org.waterwood.waterfunservicecore.api.resp.auth.CodeResult;

public interface AccountService {
    /**
     * Update password after authenticated
     * @param userUid user id
     * @param dto password update dto
     */
    void changePwd(long userUid, String verifyCodeKey, ResetPasswordDto dto);

    /**
     * Set  password
     * @param userUid user id
     * @param verifyCodeKey cached verify code key
     * @param dto password update dto
     */
    void setPassword(long userUid, String verifyCodeKey, SetPasswordDto dto);
    /**
     * Bind or activate email
     * @param userUid user id
     * @param verifyCodeKey cached verify code key
     * @param dto verify email dto
     */
    void activateEmail(long userUid, String verifyCodeKey, EmailBindActivateDto dto);

    /**
     * Change email
     * @param userUid user id
     * @param verifyCodeKey cached verify code key
     * @param dto change email dto
     */
    CodeResult changeEmail(long userUid, String verifyCodeKey, EmailChangeDto dto);

    /**
     * Bind a email for  user
     * @param userUid target user id
     * @param verifyCodeKey cached verify code key
     * @param dto bind email dto
     */
    CodeResult bindEmail(long userUid, String verifyCodeKey, EmailBindActivateDto dto);

    /**
     * Clean up unverified email
     */
    void cleanUnverifiedEmail();

    /**
     * Change phone number, only verify and check old and send activate code to new phone.
     * won't change phone number to db.
     * @param userUid user id
     * @param channelVerifyCodeKey cached verify code key
     * @param dto change phone number dto
     */
    CodeResult changePhone(long userUid, String channelVerifyCodeKey, PhoneChangeActivateDto dto);

    /**
     * Activate phone number
     * @param userUid user id
     * @param verifyCodeKey cached verify code key
     * @param dto change phone number dto
     */
    void activatePhone(long userUid, String verifyCodeKey, PhoneChangeActivateDto dto);

    /**
     * Verify user's account for Unbinding email
     *
     * @param userUid               target user id
     * @param channelVerifyCodeKey verify body
     * @param dto                  unbind email dto
     */
    void unbindEmail(long userUid, String channelVerifyCodeKey, EmailBindActivateDto dto);


}
