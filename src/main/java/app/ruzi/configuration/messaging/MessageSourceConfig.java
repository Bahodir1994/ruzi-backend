package app.ruzi.configuration.messaging;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    private static final String MESSAGES_BASE_NAME = "classpath:messages/messages";

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(MESSAGES_BASE_NAME); // Use classpath prefix
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


}
