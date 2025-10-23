package org.waterwood.waterfunservice.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Helper class for resolving messages from {@link MessageSource}.
 */
public class MessageHelper {
    private static MessageSource messageSource;
    public static void init(MessageSource messageSource) {
        MessageHelper.messageSource = messageSource;
    }

    public static String resolveMessage(String msgKey, Object[] args) {
        return messageSource.getMessage(
                msgKey,
                args,
                "Error: " + msgKey +"(No message config found)",
                LocaleContextHolder.getLocale()
        );
    }
}
