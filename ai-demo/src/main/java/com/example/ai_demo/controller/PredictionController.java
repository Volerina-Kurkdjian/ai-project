package com.example.ai_demo.controller;


import com.example.ai_demo.domain.DataPoint;
import com.example.ai_demo.dto.StockExchangeOption;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/predictions")
    public ResponseEntity<String> predictNextThreeValues(@RequestBody List<StockExchangeOption> stockExchangeOptions) throws URISyntaxException, IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        String baseUrl = "http://localhost:8080/exportCSV";


        for (StockExchangeOption so : stockExchangeOptions) {
            for (int i = 0; i < so.getNumberOfFiles(); i++) {
                String path = baseUrl + "/" + so.getStockExchange() + "/" + i;

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(path))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                String csv = response.body();
                List<DataPoint> dataPointList = getDataPointsFromCsv(csv);
                List<DataPoint> prediction = predictNext(dataPointList);

            }
        }


        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);


        String extractedData = response.getBody();

        return ResponseEntity.status(HttpStatus.OK).body("");
    }


    private List<DataPoint> getDataPointsFromCsv(String lista) {
        String[] lines = lista.split("\\r?\\n|\\r");
        List<DataPoint> list = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            for (int j = 0; j < values.length; j++) {

                values[j] = StringUtils.strip(values[j], "\"");
            }
            list.add(new DataPoint(values));
        }

        return list;
    }

    private List<DataPoint> predictNext(List<DataPoint> input){
        List<DataPoint> list = new ArrayList<>();

        return list;
    };


}


