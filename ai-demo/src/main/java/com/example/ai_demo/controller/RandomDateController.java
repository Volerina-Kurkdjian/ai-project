package com.example.ai_demo.controller;


import com.example.ai_demo.domain.DataPoint;
import com.example.ai_demo.service.DataService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping
@Component
public class RandomDateController {

    @Autowired
    private DataService dataService;

    private final String basePath = "C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\input\\";


    @GetMapping("/exportCSV/{stockExchange}/{fileIndex}")
    public ResponseEntity<StreamingResponseBody> exportCSV(
            HttpServletResponse response,
            @PathVariable String stockExchange,
            @PathVariable Integer fileIndex
    ) throws Exception {
        //pathul catre folder
        String path = basePath + stockExchange;
        if (!Files.exists(Paths.get(path))) {
            throw new RuntimeException();
        }
        // list of files in folder
        List<String> files = new ArrayList<>(Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet()));

        String stockId = files.get(fileIndex);
        String finalPath = path + "\\" + stockId;//path catre fisierul csv


        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("data.csv").build());

        return new ResponseEntity<>(outputStream -> {

            Writer writer = new OutputStreamWriter(outputStream);
            CSVWriter csvWriter = new CSVWriter(writer);
            List<DataPoint> list = null;
            try {
                list = dataService.getConsecutiveDataPoints(
                        Paths.get(finalPath)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            addCsv(csvWriter, list);
            csvWriter.close();
            writer.flush();
        }, headers, HttpStatus.OK);

    }


    @GetMapping("/export-csv")
    public ResponseEntity<StreamingResponseBody> exportCsv() {
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.TEXT_CSV);
        headers.setContentDisposition(ContentDisposition.attachment().filename("data.csv").build());
        return new ResponseEntity<>(outputStream -> {
            Writer writer = new OutputStreamWriter(outputStream);
            CSVWriter csvWriter = new CSVWriter(writer);


            List<DataPoint> list = null;
            try {
                list = dataService.getConsecutiveDataPoints(
                        Paths.get("C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\input\\LSEG\\FLTR.csv")
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            addCsv(csvWriter, list);

            csvWriter.close();
            writer.flush();
        }, headers, HttpStatus.OK);
    }


    public void addCsv(CSVWriter writer, List<DataPoint> data) {
        for (DataPoint dataItem : data) {
            writer.writeNext(dataItem.toCsvLine());
        }
    }


    public static void createCsv(List<DataPoint> datas, String fileName) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {


        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            if (!datas.isEmpty()) {
                for (DataPoint line : datas) {
                    String[] csvLine = line.toCsvLine();
                    if (csvLine != null) {
                        writer.writeNext(csvLine);
                    } else {
                        System.err.println("Invalid DataPoint: " + line);
                    }
                }
            } else {
                System.err.println("Empty data list");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file I/O errors appropriately
        }

    }


}







