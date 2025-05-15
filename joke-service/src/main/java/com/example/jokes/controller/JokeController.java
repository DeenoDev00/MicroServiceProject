package com.example.jokes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/joke")
public class JokeController {

    private static final List<String> JOKES = List.of(
            "Why don't scientists trust atoms? Because they make up everything!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why don't eggs tell jokes? They'd crack each other up.",
            "What do you call a fish with no eyes? Fsh!",
            "Why don't skeletons fight? They don't have the guts."
    );

    private final Random random = new Random();

    @GetMapping("/random")
    public String getRandomJoke() {
        int index = random.nextInt(JOKES.size());
        return JOKES.get(index);
    }
}
