package org.waterwood.waterfunservice.confirguation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.utils.MessageHelper;

@Configuration
public class MessageConfig {
    @Autowired
    public void initMessageUtils(MessageSource messageSource) {
        MessageHelper.init(messageSource);
    }
}
