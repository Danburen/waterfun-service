package org.waterwood.waterfunservice.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.waterwood.waterfunservice.infrastructure.validation.PhoneNumber;

@Data
public class SendSmsCodeRequest {
    @PhoneNumber
    @NotBlank(message = "{validation.phone.invalid}")
    private String phoneNumber;
    private CodePurpose purpose;
}
