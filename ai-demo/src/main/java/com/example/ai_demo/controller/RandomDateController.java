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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping
public class RandomDateController {

         @Autowired
        private DataService dataService;

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

       createCsv(list,"com/example/ai_demo/output");

    }

    public static void createCsv(List<DataPoint> datas, String fileName) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            StatefulBeanToCsv<DataPoint> beanToCsv = new StatefulBeanToCsvBuilder<DataPoint>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(datas);

        }
    }


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


}



