package org.waterwood.waterfunservice.DTO.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.waterwood.waterfunservice.service.authServices.AuthErrorCode;

/**
 * Represents the result of sending an SMS code.
 */
@Getter
@AllArgsConstructor
@Builder
public class SmsCodeResult{
    private final Boolean trySendSuccess;
    private @Nullable final String msg;
    private @Nullable final AuthErrorCode authErrorCode;
    private @Nullable final SmsCodeSendResult result;
}
