package com.example.quotes.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    private static final List<String> QUOTES = List.of(
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Strive not to be a success, but rather to be of value. - Albert Einstein",
            "The mind is everything. What you think you become. - Buddha",
            "Life is what happens when you're busy making other plans. - John Lennon",
            "The greatest glory in living lies not in never falling, but in rising every time we fall. - Nelson Mandela"
    );

    private final Random random = new Random();

    @GetMapping("/random")
    public String getRandomQuote() {
        int index = random.nextInt(QUOTES.size());
        return QUOTES.get(index);
    }
}
