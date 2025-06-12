package org.waterwood.waterfunservice.DTO.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
@Builder
public class EmailCodeSendResult {
    private final Boolean sendSuccess;
    private final String email;
    private final @Nullable String message;
    private final @Nullable String responseRaw;
}
