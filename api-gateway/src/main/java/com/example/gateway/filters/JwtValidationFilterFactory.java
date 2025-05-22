package com.example.gateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtValidationFilterFactory extends AbstractGatewayFilterFactory<JwtValidationFilterFactory.Config> {

    private final JwtValidationFilter jwtValidationFilter;

    @Autowired
    public JwtValidationFilterFactory(JwtValidationFilter jwtValidationFilter) {
        super(Config.class);
        this.jwtValidationFilter = jwtValidationFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtValidationFilter;
    }

    public static class Config {
        // Future config values, optional
    }
}
