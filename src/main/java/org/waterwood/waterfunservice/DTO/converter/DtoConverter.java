package org.waterwood.waterfunservice.DTO.converter;

import org.springframework.stereotype.Component;

public interface DtoConverter<S,T> {
    public T convert(S source);
}
