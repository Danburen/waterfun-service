package org.waterwood.waterfunservice.DTO.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

@Getter
@AllArgsConstructor
@Builder
public class OperationResult<T> {
    private boolean trySuccess;
    private @Nullable final ServiceErrorCode serviceErrorCode;
    private @Nullable final ResponseCode responseCode;
    private @Nullable final ErrorType errorType;
    private @Nullable final String message;
    T resultData;
}
