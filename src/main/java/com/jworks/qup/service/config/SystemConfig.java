package com.jworks.qup.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Johnpaul Chukwu.
 * @since 24/09/2021
 */

@Configuration
public class SystemConfig {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
