package org.waterwood.waterfunservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.waterwood.common.validation.StrongPassword;
import org.waterwood.waterfunservicecore.api.req.auth.SecurityVerifyCodeDto;

@Data
public class SetPasswordDto {
    @NotBlank
    @StrongPassword
    private String newPwd;
    @NotBlank
    private String confirmPwd;
    @NotNull
    private SecurityVerifyCodeDto verify;
}
