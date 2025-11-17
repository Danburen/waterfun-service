package org.waterwood.waterfunservice.dto.response.auth;

import lombok.Value;

import java.io.Serializable;

@Value
public class LoginClientData implements Serializable {
    String accessToken;
    Long exp;
}
