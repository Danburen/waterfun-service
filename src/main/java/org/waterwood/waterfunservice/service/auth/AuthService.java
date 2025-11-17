package org.waterwood.waterfunservice.service.auth;

import org.waterwood.waterfunservice.dto.common.TokenPair;
import org.waterwood.waterfunservice.dto.common.TokenResult;

public interface AuthService {
    TokenPair createNewTokens(long userId, String deviceFingerprint);
    TokenResult refreshAccessToken(String refreshToken, String dfp);
}
