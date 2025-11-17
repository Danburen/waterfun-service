package org.waterwood.waterfunservice.service.auth;

import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.dto.request.auth.RegisterRequest;
import org.waterwood.waterfunservice.entity.user.User;

public interface RegisterService {
    @Transactional
    User register(RegisterRequest body, String smsCodeKey);
}
