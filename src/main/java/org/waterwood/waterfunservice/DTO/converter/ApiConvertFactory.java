package org.waterwood.waterfunservice.DTO.converter;

import org.waterwood.waterfunservice.DTO.common.ApiResponse;

public class ApiConvertFactory {
    public static <T,R>ApiResponse<R> convert(ApiResponse<T> source, DtoConverter<T,R> converter){
        if(source == null) return null;
        return new ApiResponse<>(source.getCode(),source.getMessage(),
                source.getData() != null ? converter.convert(source.getData()) : null);
    }
}
