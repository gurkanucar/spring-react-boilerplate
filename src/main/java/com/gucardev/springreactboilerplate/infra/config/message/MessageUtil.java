package com.gucardev.springreactboilerplate.infra.config.message;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    private static MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String key, Object[] args) {
        if (messageSource == null) {
            return key;
        }
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            try {
                return messageSource.getMessage("messages.fallback", null, LocaleContextHolder.getLocale());
            } catch (Exception ignored) {
                return key;
            }
        }
    }

    public static String getMessage(String key) {
        return getMessage(key, null);
    }

}

