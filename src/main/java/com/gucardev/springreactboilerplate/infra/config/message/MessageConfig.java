package com.gucardev.springreactboilerplate.infra.config.message;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.of("tr", "TR")));
        resolver.setDefaultLocale(Locale.of("tr", "TR"));
        return resolver;
    }

    /**
     * Makes Bean Validation resolve constraint message keys (e.g.
     * {@code {sort.direction.pattern.exception}}) from the application {@link MessageSource}
     * — the same {@code messages*.properties} used everywhere else — instead of a separate
     * {@code ValidationMessages.properties}. Built-in constraint messages still fall back to
     * the validator defaults.
     */
    @Bean
    public LocalValidatorFactoryBean defaultValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }
}
