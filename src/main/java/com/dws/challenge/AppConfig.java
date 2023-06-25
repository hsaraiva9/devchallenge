package com.dws.challenge;

import com.dws.challenge.domain.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dws.challenge.service.NotificationService;

@Configuration
public class AppConfig {
    @Bean
    public NotificationService notificationService() {
        return new NotificationService() {
            @Override
            public void notifyAboutTransfer(Account account, String transferDescription) {

            }
        };
    }
}
