package org.waterwood.waterfunservice.DTO.converter;

import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.DTO.response.LoginClientResponse;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;

@Component
public class LoginResponseConverter implements DtoConverter<LoginServiceResponse, LoginClientResponse> {
    @Override
    public LoginClientResponse convert(LoginServiceResponse source) {
        return new LoginClientResponse(
                source.getUserId(),
                source.getUsername(),
                source.getExpiresIn()
        );
    }
}
