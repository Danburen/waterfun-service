package org.waterwood.waterfunservice.DTO.request;

import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.CodePurpose;

@Data
public class SendEmailCodeRequest {
    private String email;
    private CodePurpose purpose;
}
