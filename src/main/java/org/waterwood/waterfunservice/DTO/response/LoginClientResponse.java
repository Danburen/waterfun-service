package org.waterwood.waterfunservice.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;

/**
 * @see LoginServiceResponse
 */
@Data
@AllArgsConstructor
public class LoginClientResponse {
    private Long userId;
    private String username;
    private Long expireIn;
}
