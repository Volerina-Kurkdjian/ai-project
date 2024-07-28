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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;



@RestController
@RequestMapping
@Component
public class RandomDateController {

         @Autowired
        private DataService dataService;


//         BeanToCsvMappingStrategy<DataPoint> strategy = new BeanToCsvMappingStrategy<>();
//        strategy.setType(DataPoint.class);

//        @GetMapping("/csv/{filename}")
//        public ResponseEntity<byte[]> getCsv(@PathVariable String filename) throws IOException {
//            ClassPathResource resource = new ClassPathResource("static/csv/" + filename + ".csv");
//            byte[] content = resource.getInputStream().readAllBytes();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDisposition(ContentDisposition.ATTACHMENT.toString() + "; filename=\"" + filename + ".csv\"");
//
//            return new ResponseEntity<>(content, headers, HttpStatus.OK);
//        }

    @GetMapping("/exportCSV")
    public void exportCSV(HttpServletResponse response) throws Exception {
        String fileName = "FLTR.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "");

        StatefulBeanToCsv<Files> writer = new StatefulBeanToCsvBuilder<Files>(response.getWriter())
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        List<DataPoint> list= dataService.getConsecutiveDataPoints(
            Paths.get("C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\LSEG\\FLTR.csv")
        );

       createCsv(list,"C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\output\\out.csv");

    }


    @GetMapping("/export-csv")
    public ResponseEntity<StreamingResponseBody> exportCsv() {
        HttpHeaders headers = new HttpHeaders();
       // headers.setContentType(MediaType.TEXT_CSV);
        headers.setContentDisposition(ContentDisposition.attachment().filename("data.csv").build());

        return new ResponseEntity<>(outputStream -> {
            Writer writer = new OutputStreamWriter(outputStream);
            CSVWriter csvWriter = new CSVWriter(writer);


            List<DataPoint> list= null;
            try {
                list = dataService.getConsecutiveDataPoints(
                        Paths.get("C:\\Users\\corina\\Downloads\\ai-demo\\ai-demo\\src\\main\\java\\com\\example\\ai_demo\\LSEG\\FLTR.csv")
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            addCsv(csvWriter,  list);

            csvWriter.close();
            writer.flush();
        }, headers, HttpStatus.OK);
    }


    public void addCsv(CSVWriter writer, List<DataPoint> data){
        for (DataPoint dataItem : data) {
               writer.writeNext(dataItem.toCsvLine() );
        }
    }


    public static void createCsv(List<DataPoint> datas, String fileName) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {


//            StatefulBeanToCsv<DataPoint> beanToCsv = new StatefulBeanToCsvBuilder<DataPoint>(writer)
//                    .withMappingStrategy()
//                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
//                    .build();
//            beanToCsv.write(datas);

//        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
//            for (DataPoint line : datas) {
//                writer.writeNext(line.toCsvLine());
//            }
//        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            if (!datas.isEmpty()) {
                for (DataPoint line : datas) {
                    String[] csvLine =line.toCsvLine();
                    if (csvLine != null ) {
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

//    public String writeLineByLine(List<String[]> lines, Path path) throws Exception {
//        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
//            for (String[] line : lines) {
//                writer.writeNext(line);
//            }
//            return Helpers.readFile(path);
//        }


//    @GetMapping("/users/csv-stream")
//    public ResponseEntity<StreamingResponseBody> getUsersAsCsvStream(List<DataPoint> users) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE));
//        headers.setContentDisposition("attachment; filename=\"users.csv\"");
//
//        StreamingResponseBody responseBody = outputStream -> {
//            Writer writer = new OutputStreamWriter(outputStream);
//            // Write CSV content using OpenCSV or other methods
//
//            StatefulBeanToCsv<Files> writer = new StatefulBeanToCsvBuilder<Files>(response.getWriter())
//                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
//                    .withOrderedResults(true)
//                    .build();
//
//            List<DataPoint> list= dataService.getConsecutiveDataPoints(fileName);
//
//            writer.flush();
//        };
//
//        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
//    }






