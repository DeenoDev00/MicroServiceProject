package com.example.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.auth.model.UserCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private static final String HARDCODED_USERNAME = "user";
    private static final String HARDCODED_PASSWORD = "password";
    private static final String JWT_SECRET = "your-secret-key"; // Replace with a strong, environment-specific secret
    private static final long JWT_EXPIRATION_MS = 3600000; // 1 hour

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials credentials) {
        if (HARDCODED_USERNAME.equals(credentials.getUsername()) &&
                HARDCODED_PASSWORD.equals(credentials.getPassword())) {

            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            String token = JWT.create()
                    .withSubject(credentials.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                    .withClaim("roles", "user") // Example role
                    .sign(algorithm);

            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
