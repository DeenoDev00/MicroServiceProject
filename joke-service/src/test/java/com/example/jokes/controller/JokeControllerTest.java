package com.example.jokes.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JWT_SECRET = "your-256-bit-secret";

    @Test
    public void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/random"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnJokeWithValidToken() throws Exception {
        String token = JWT.create()
                .withSubject("testuser")
                .withClaim("roles", "user")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.HMAC256(JWT_SECRET));

        mockMvc.perform(get("/random")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

