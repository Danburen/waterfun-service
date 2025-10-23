package org.waterwood.waterfunservice.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    private final MessageSource messageSource;
    private final Map<String, String> messageCache = new ConcurrentHashMap<>();
    @Getter
    private static MessageService instance;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
    }

    public String getMessage(ResponseCode code, Object... args) {
        return getMessage(code.name(), args);
    }

    @PostConstruct
    public void preloadMessages() {
        Arrays.stream(ResponseCode.values())
                .forEach(this::getMessage);
    }
}
