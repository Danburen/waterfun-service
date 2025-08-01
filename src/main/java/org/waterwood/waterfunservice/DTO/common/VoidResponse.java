package org.waterwood.waterfunservice.DTO.common;

public class VoidResponse extends ApiResponse<Void> {
    public VoidResponse(Integer code, String message, Void data) {
        super(code, message, data);
    }
}
