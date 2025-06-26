package org.waterwood.waterfunservice.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.response.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponseData;
import org.waterwood.waterfunservice.DTO.common.result.OpResult;

public class ResponseUtil {
    public static void setCookieAndNoCache(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        response.setContentType("application/json");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setDateHeader("Expires", 0);
    }

    public static void setTokenCookie(HttpServletResponse response, LoginResponseData data) {
        ResponseCookie accessCookie = ResponseCookie.from("access_Token",data.getAccessToken())
                .httpOnly(true)
                .secure(true) // only https
                .sameSite("Strict")
                .maxAge(data.getExpiresIn())
                .path("/")
                .build();
        response.addHeader("Set-Cookie",accessCookie.toString());
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", data.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)  // same with jwt refresh token
                .path("/auth/refresh")
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> response) {
        int httpStatus = ResponseCode.toHttpStatus(response.getCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(OpResult<T> opResult) {
        int httpStatus;
        if (opResult.getResponseCode() != null) {
            httpStatus = opResult.getResponseCode().getCode();
        }else{
            httpStatus = opResult.isTrySuccess() ? 200 : 500;
        }
        return ResponseEntity.status(httpStatus).body(
                new ApiResponse<>(httpStatus,opResult.getMessage(),opResult.getResultData())
        );
    }
}
