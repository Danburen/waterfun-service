package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.CodePurpose;

@Data
public class SendSmsCodeRequest {
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("purpose")
    private CodePurpose purpose;
}
