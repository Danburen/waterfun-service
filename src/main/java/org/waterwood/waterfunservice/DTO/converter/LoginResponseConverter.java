package org.waterwood.waterfunservice.DTO.converter;

import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.DTO.response.LoginClientData;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;

@Component
public class LoginResponseConverter implements DtoConverter<LoginServiceResponse, LoginClientData> {
    @Override
    public LoginClientData convert(LoginServiceResponse source) {
        return new LoginClientData(
                source.getUserId(),
                source.getUsername(),
                source.getExpireIn()
        );
    }
}
