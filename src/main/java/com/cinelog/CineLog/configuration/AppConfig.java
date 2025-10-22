package com.cinelog.CineLog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        // Create a request factory using the Apache HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        // You can set timeouts here if you want, which is a good practice
        requestFactory.setConnectTimeout(5000); // 5 seconds
        requestFactory.setConnectionRequestTimeout(5000); // 5 seconds

        // Create a RestTemplate that uses this new factory
        return new RestTemplate(requestFactory);
    }
}
