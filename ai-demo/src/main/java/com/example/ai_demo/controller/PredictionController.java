package com.example.ai_demo.controller;


import com.example.ai_demo.dto.StockExchangeOption;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Component
public class PredictionController {


//    @Autowired
//    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private String otherApiBaseUrl;

    @GetMapping("/predictions")
    public ResponseEntity<String> predictNextThreeValues(@RequestBody List<StockExchangeOption> stockExchangeOptions){
        RestTemplate restTemplate = new RestTemplate();

        // Make a request to the first API
     //   ResponseEntity<String> response1 = restTemplate.getForEntity("https://api1.example.com/data", String.class);

        // Parse the response
        // Assuming the response is JSON, use a JSON library to parse it
        // Extract relevant data

        // Construct a request to the second API using the extracted data
     //   String requestBody = // ... construct request body using extracted data

                // Make a request to the second API
     //           ResponseEntity<String> response2 = restTemplate.postForEntity("https://api2.example.com/process", requestBody, String.class);

        // Handle the response from the second API

        String url = otherApiBaseUrl+"http://localhost:8080/exportCsv";

        Map<String, Integer> peopleMap = new HashMap<>();

        // Iterate over the list and populate the HashMap
        for (StockExchangeOption person : stockExchangeOptions) {
            peopleMap.put(person.getStockExchange(), person.getNumberOfFiles());
        }

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String extractedData = response.getBody();

        return  ResponseEntity.status(HttpStatus.OK).body(extractedData);
    }
}
