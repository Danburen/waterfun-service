package org.waterwood.waterfunservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginServiceResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private Long expireIn; // seconds
}
