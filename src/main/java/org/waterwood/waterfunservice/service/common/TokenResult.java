package org.waterwood.waterfunservice.service.common;

/**
 * TokenResult is a record that holds the result of a accessToken generation operation.
 * @param accessToken generated accessToken
 * @param expire expire time in <b>Seconds</b> (TTL)
 */
public record TokenResult(String accessToken, Long expire) {
}
