package org.waterwood.waterfunservice.DTO.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponseData {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private Long expiresIn;
}
