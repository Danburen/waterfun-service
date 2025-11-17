package org.waterwood.waterfunservice.service.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.infrastructure.exception.business.AuthException;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.dto.common.TokenPair;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.dto.common.TokenResult;
import org.waterwood.waterfunservice.service.auth.AuthService;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;
import org.waterwood.waterfunservice.infrastructure.utils.StringUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RSAJwtTokenService tokenService;
    private final DeviceServiceImpl deviceService;
    private final UserRepository userRepository;

    @Override
    public TokenPair createNewTokens(long userId, String deviceFingerprint) {
        String deviceId = deviceService.generateAndStoreDeviceId(userId,deviceFingerprint);
        TokenResult accessToken = tokenService.generateStoreNewAndRevokeOthers(userId,deviceId);
        TokenResult refreshToken = tokenService.generateAndStoreRefreshToken(userId,deviceId);
        return new TokenPair(
                accessToken.tokenValue(), accessToken.expire(),
                refreshToken.tokenValue(), refreshToken.expire());
    }

    /**
     * Return the api response of refresh access tokenValue operation.
     * <p>for future extension or refactor , we temporarily use api response instead of OpResult</p>
     * @param refreshToken refresh tokenValue
     * @return ServiceResult type Token result that contains tokenValue and expirations.
     *
     */
    @Override
    public TokenResult refreshAccessToken(String refreshToken, String dfp) {
        if(StringUtil.isBlank(refreshToken)) { // Missing refresh token
            throw new AuthException(ResponseCode.REAUTHENTICATE_REQUIRED);
        }
        RefreshTokenPayload payload = tokenService.validateRefreshToken(refreshToken,dfp);
        long userId = payload.userId();
        String deviceId = payload.deviceId();
        return userRepository.findById(userId).map(_->
                tokenService.RegenerateRefreshToken(refreshToken,userId,deviceId))
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));
    }

}
