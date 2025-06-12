package org.waterwood.waterfunservice.DTO.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LoginType {
    @JsonProperty("password") PASSWORD,
    @JsonProperty("sms") SMS,
    @JsonProperty("email") EMAIL
}
