package org.waterwood.waterfunservice.service.common;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException() {
        super("Refresh token expired: ");
    }
}
