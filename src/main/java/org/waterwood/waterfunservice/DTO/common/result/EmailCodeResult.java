package org.waterwood.waterfunservice.DTO.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.waterwood.waterfunservice.service.authServices.AuthErrorCode;

@Getter
@AllArgsConstructor
@Builder
public class EmailCodeResult {
    private final Boolean trySendSuccess;
    private @Nullable final String msg;
    private @Nullable final AuthErrorCode authErrorCode;
    private @Nullable final EmailCodeSendResult result;
}
