package com.example.ai_demo.controller;


import com.example.ai_demo.domain.DataPoint;
import com.example.ai_demo.dto.StockExchangeOption;
import com.example.ai_demo.service.DataService;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public ResponseEntity<byte[]> predictNextThreeValues(@RequestBody List<StockExchangeOption> stockExchangeOptions) throws URISyntaxException, IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        String baseUrl = "http://localhost:8080/exportCSV";
        String tmpPath="C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\output";
        String predictionPath = tmpPath+"\\predictions";
        (new File(predictionPath)).mkdir();


        for (StockExchangeOption so : stockExchangeOptions) {

            // make stock exchange dir
            (new File(predictionPath + "\\" + so.getStockExchange())).mkdir();

            for (int i = 0; i <= so.getNumberOfFiles() - 1; i++) {
                String path = baseUrl + "/" + so.getStockExchange() + "/" + i;

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(path))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    continue;
                }

                String csv = response.body();
                List<DataPoint> dataPointList = getDataPointsFromCsv(csv);
                List<DataPoint> prediction = predictNext(dataPointList);

                // stock exchange path + stockId (that we get from prediction list)
                String csvOutput = predictionPath + "\\" + so.getStockExchange() + "\\" +
                        prediction.get(0).getStockID() + ".csv";

                // save csv:
                Writer writer = new FileWriter(csvOutput);
                CSVWriter csvWriter = new CSVWriter(writer);
                addCsv(csvWriter, prediction);
                csvWriter.flush();

            }
        }

        // zip folder
        zipFolder(predictionPath, tmpPath + "\\predictions.zip");

        // cleanup
        Path path = Paths.get(predictionPath);
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);



        byte[] fileContent = Files.readAllBytes(
            Paths.get(tmpPath + "\\predictions.zip")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("predictions.zip").build());


        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

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

    /**
     * Output Format
     * One .csv output file for each file processed. Each .csv file should have 3 columns on each row as shown below.
     * Timestamp & stock price have same format as input file
     * Stock-ID, Timestamp-1, stock price 1
     * ..
     * Stock-ID, Timestamp-n, stock price n
     * Stock-ID, Timestamp-n+1, stock price n+1
     * Stock-ID, Timestamp-n+2, stock price n+2
     * Stock-ID, Timestamp-n+3, stock price n+3
     *
     * @param input
     * @return
     */
    private List<DataPoint> predictNext(List<DataPoint> input) {
        List<DataPoint> list = new ArrayList<>();
        DataPoint firstPrediction;
        DataPoint secondPrediction;
        DataPoint thirdPrediction;

        DataPoint lastValue = input.get(input.size() - 1);

        Float highestPrice = 0F, secondHighestprice = 0F;
        for (int i = 0; i < input.size() - 1; i++) {
            if (input.get(i).getPriceValue() > highestPrice) {
                secondHighestprice = highestPrice;
                highestPrice = input.get(i).getPriceValue();
            }

        }

        firstPrediction = new DataPoint();
        firstPrediction.setPriceValue(secondHighestprice);
        firstPrediction.setTimestamp(lastValue.getTimestamp().plusDays(1));
        firstPrediction.setStockID(lastValue.getStockID());


        secondPrediction = new DataPoint();
        secondPrediction.setPriceValue((Math.abs((secondHighestprice - lastValue.getPriceValue())) / 2));
        secondPrediction.setTimestamp(lastValue.getTimestamp().plusDays(2));
        secondPrediction.setStockID(lastValue.getStockID());

        thirdPrediction = new DataPoint();
        thirdPrediction.setPriceValue(Math.abs(secondPrediction.getPriceValue() - firstPrediction.getPriceValue()) / 4);
        thirdPrediction.setTimestamp(lastValue.getTimestamp().plusDays(3));
        thirdPrediction.setStockID(lastValue.getStockID());

        input.add(firstPrediction);
        input.add(secondPrediction);
        input.add(thirdPrediction);

        return input;
    }

    public  void addCsv(CSVWriter writer, List<DataPoint> data) {
        for (DataPoint dataItem : data) {
            writer.writeNext(dataItem.toCsvLine());
        }
    }


    public static void zipFolder(String sourceFolder, String zipFile) throws IOException {
        Path sourceDir = Paths.get(sourceFolder);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));

        Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String zipEntryName = sourceDir.relativize(path).toString().replace("\\", "/");
                        ZipEntry zipEntry = new ZipEntry(zipEntryName);
                        zos.putNextEntry(zipEntry);

                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        zos.close();
    }


}


