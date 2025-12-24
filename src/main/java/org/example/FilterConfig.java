package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter(
            RateLimiter rateLimiter
    ) {

        FilterRegistrationBean<RateLimitingFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(new RateLimitingFilter(rateLimiter));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);

        return registration;
    }
}
