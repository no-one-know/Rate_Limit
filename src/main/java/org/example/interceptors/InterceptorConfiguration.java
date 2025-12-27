package org.example.interceptors;

import org.example.RateLimiter;
import org.example.properties.RateLimiterProperties;
import org.example.utils.EndpointRateLimitResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorConfiguration {

    @Bean
    public FilterRegistrationBean<RequestLoggingInterceptor> loggingFilter() {
        FilterRegistrationBean<RequestLoggingInterceptor> reg =
                new FilterRegistrationBean<>();
        reg.setFilter(new RequestLoggingInterceptor());
        reg.addUrlPatterns("/*");
        reg.setOrder(InterceptorOrder.FIRST.ordinal());
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RequestRateLimitingInterceptor> rateLimitingFilter(
            RateLimiter rateLimiter,
            RateLimiterProperties rateLimiterProperties,
            EndpointRateLimitResolver endpointRateLimitResolver
    ) {

        FilterRegistrationBean<RequestRateLimitingInterceptor> registration =
                new FilterRegistrationBean<>();
        registration.setFilter(new RequestRateLimitingInterceptor(rateLimiter, rateLimiterProperties, endpointRateLimitResolver));
        registration.addUrlPatterns("/*");
        registration.setOrder(InterceptorOrder.SECOND.ordinal());
        return registration;
    }
}
