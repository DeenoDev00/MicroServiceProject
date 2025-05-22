package com.example.gateway.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtValidationFilter implements GatewayFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);
    private final String secretKey = "your-256-bit-secret"; // Same as in AuthController

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer ".length()
            try {
                DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey))
                        .build()
                        .verify(token);

                logger.info("JWT is valid for subject: {}", jwt.getSubject());
                // Token is valid, proceed with the request
                return chain.filter(exchange);
            } catch (JWTVerificationException e) {
                logger.warn("Invalid or expired JWT: {}", e.getMessage());
                // Token is invalid or expired
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            logger.warn("Missing or invalid Authorization header: {}", authHeader);
            // Authorization header is missing or invalid
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // Filter should run early in the chain
    }
}
