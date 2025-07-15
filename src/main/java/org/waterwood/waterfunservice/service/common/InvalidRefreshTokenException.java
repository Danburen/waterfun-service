package org.waterwood.waterfunservice.service.common;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Illegal refresh token");
    }
}
