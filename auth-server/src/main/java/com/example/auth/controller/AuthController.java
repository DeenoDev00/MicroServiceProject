package com.example.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.auth.model.UserCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String HARDCODED_USERNAME = "user";
    private static final String HARDCODED_PASSWORD = "$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW"; // "password" encoded with BCrypt
    private static final String JWT_SECRET = "your-256-bit-secret"; // Using the same secret as in docker-compose.yml
    private static final long JWT_EXPIRATION_MS = 3600000; // 1 hour

    private final PasswordEncoder passwordEncoder;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials credentials) {
        if (HARDCODED_USERNAME.equals(credentials.getUsername()) &&
                passwordEncoder.matches(credentials.getPassword(), HARDCODED_PASSWORD)) {

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
