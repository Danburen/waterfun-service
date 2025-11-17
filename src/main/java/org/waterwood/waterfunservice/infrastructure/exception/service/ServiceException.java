package org.waterwood.waterfunservice.infrastructure.exception.service;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
