package org.waterwood.waterfunservice.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;

public class CookieUtil {
    private static final String COOKIE_SAME_SITE_CONFIG = "Strict";
    private static final boolean COOKIE_SECURE = false;
    public static void setTokenCookie(HttpServletResponse response, LoginServiceResponse data) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN",data.getAccessToken())
                .httpOnly(true)
                .secure(COOKIE_SECURE) // only https
                .sameSite(COOKIE_SAME_SITE_CONFIG)
                .maxAge(data.getExpireIn())
                .path("/")
                .build();
        response.addHeader("Set-Cookie",accessCookie.toString());
        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", data.getRefreshToken())
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE_CONFIG)
                .maxAge(7 * 24 * 60 * 60)  // same with jwt refresh token
                .path("/auth/refresh")
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    public static void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE_CONFIG)
                .maxAge(0)  // Instance expired
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE_CONFIG)
                .maxAge(0)  // Instance expired
                .path("/auth/refresh")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}
