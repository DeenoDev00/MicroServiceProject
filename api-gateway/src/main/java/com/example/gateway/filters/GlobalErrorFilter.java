package com.example.gateway.filters;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
@Order(-1) // Highest priority
public class GlobalErrorFilter implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        HttpStatus status;
        String errorMessage;

        if (ex instanceof ResponseStatusException) {
            status = HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
            errorMessage = ex.getMessage();
        } else {
            // Handle other exceptions (e.g., JWTVerificationException)
            if (ex instanceof JWTVerificationException) {
                status = HttpStatus.UNAUTHORIZED;
                errorMessage = "Invalid JWT Token: " + ex.getMessage();
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Internal Server Error: " + ex.getMessage();
            }
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = bufferFactory.wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
