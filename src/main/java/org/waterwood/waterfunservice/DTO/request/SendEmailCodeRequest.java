package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.CodePurpose;

@Data
public class SendEmailCodeRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("purpose")
    private CodePurpose purpose;
}
