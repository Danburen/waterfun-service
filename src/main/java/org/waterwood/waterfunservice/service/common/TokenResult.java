package org.waterwood.waterfunservice.service.common;

/**
 * TokenResult is a record that holds the result of a token generation operation.
 * @param token generated token
 * @param expireIn expire time in <b>Seconds</b>>
 */
public record TokenResult(String token, Long expireIn) {
}
