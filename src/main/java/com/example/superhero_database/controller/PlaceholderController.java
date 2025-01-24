package com.example.superhero_database.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/placeholder")
public class PlaceholderController {

    @GetMapping
    public ResponseEntity<?> getPlaceholder(@RequestParam int width, @RequestParam int height, @RequestParam String text) {
        // Generate or serve a static placeholder image
        String placeholderUrl = "https://via.placeholder.com/" + width + "x" + height + "?text=" + text;
        return ResponseEntity.status(302).header("Location", placeholderUrl).build(); // Redirect to an external placeholder service
    }
}
