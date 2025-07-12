package org.waterwood.waterfunservice.DTO.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LoginType {
    @JsonProperty("sms") SMS,
    @JsonProperty("password") PASSWORD,
    @JsonProperty("email") EMAIL
}
