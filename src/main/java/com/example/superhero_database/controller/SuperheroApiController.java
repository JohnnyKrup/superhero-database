package com.example.superhero_database.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/superheroapi")
@Slf4j
public class SuperheroApiController {

    @Value("${superhero.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public SuperheroApiController() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSuperhero(@PathVariable String id) {
        String apiUrl = String.format("https://superheroapi.com/api/%s/%s", apiKey, id);

        try {
            Object response = restTemplate.getForObject(apiUrl, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching superhero: " + e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchSuperheroes(@PathVariable String name) {
        String apiUrl = String.format("https://superheroapi.com/api/%s/search/%s", apiKey, name);

        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(apiUrl, Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Error searching superheroes: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<?> getSuperheroImage(@PathVariable String id) {
        String apiUrl = String.format("https://superheroapi.com/api/%s/%s/image", apiKey, id);

        System.out.println("Fetching superhero image for ID: " + id);
        System.out.println("Using URL: " + apiUrl);

        try {
            // Configure headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // Create HTTP entity with headers
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Make the request
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    Map.class
            );

            // Log the response
            System.out.println("Received response from superhero API");
            System.out.println("Response body: " + response.getBody());


            return ResponseEntity
                    .ok(response.getBody());

        } catch (Exception e) {
            log.error("Error fetching superhero: ", e);
            System.err.println("Error in getSuperheroImage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Error fetching superhero: " + e.getMessage(),
                            "details", e.toString()
                    ));
        }
    }

    /**
     * Get the power stats of the Hero
     * Intelligence
     * Strength
     * Speed
     * Durability
     * Power
     * Combat
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/powerstats")
    public ResponseEntity<?> getHeroPowerStats(@PathVariable String id) {
        String apiUrl = String.format("https://superheroapi.com/api/%s/%s/powerstats", apiKey, id);

        try {
            Object response = restTemplate.getForObject(apiUrl, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching hero stats: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Random Hero selector Method
     * Lets the player select one hero out of @param count heroes.
     *
     * @param count
     * @return selected Hero
     */
    @GetMapping("/random")
    public ResponseEntity<?> getRandomHeroes(@RequestParam(defaultValue = "3") int count) {
        try {
            // SuperheroAPI has 731 heroes total
            List<Object> heroes = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int randomId = (int) (Math.random() * 731) + 1;
                String apiUrl = String.format("https://superheroapi.com/api/%s/%d", apiKey, randomId);
                Object hero = restTemplate.getForObject(apiUrl, Object.class);
                heroes.add(hero);
            }
            return ResponseEntity.ok(heroes);
        } catch (Exception e) {
            log.error("Error fetching random heroes: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
