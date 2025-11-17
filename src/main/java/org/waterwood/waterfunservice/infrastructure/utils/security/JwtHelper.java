package org.waterwood.waterfunservice.infrastructure.utils.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.infrastructure.exception.business.BusinessException;
import org.waterwood.waterfunservice.infrastructure.exception.service.ServiceException;

/**
 * Use ThreadLocal instead of get Authentication from SecurityContextHolder
 */
@Deprecated
public class JwtHelper {
    @Deprecated
    public static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }
        return null;
    }

    @Deprecated
    public static String getCurrentClaim(String claimName) {
        Jwt jwt = getCurrentJwt();
        if(jwt == null){
            throw new BusinessException(ResponseCode.HTTP_UNAUTHORIZED);
        }
        if(jwt.getClaim(claimName) == null){
            throw new ServiceException("Claim not found");
        }
        return  jwt.getClaim(claimName);
    }
    @Deprecated
    public static Long getCurrentUserId() {
        return  Long.parseLong(getCurrentClaim("sub"));
    }
}
