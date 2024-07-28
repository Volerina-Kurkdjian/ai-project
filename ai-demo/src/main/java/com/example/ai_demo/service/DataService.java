package com.example.ai_demo.service;

import com.example.ai_demo.domain.DataPoint;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class DataService {

    public List<DataPoint> getConsecutiveDataPoints(Path filePath) throws Exception {

        List<DataPoint> list = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    list.add(new DataPoint(line));
                }
            }
        }



//       List<DataPoint> dataPoints=new ArrayList<>();

        LocalDateTime startTimestamp = generateRandomTimestamp(list);

        return findConsecutiveDataPoints(list, startTimestamp);
    }


    List<DataPoint> readCsvData(String filepath) throws IOException {
        ColumnPositionMappingStrategy<DataPoint> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(DataPoint.class);
        // map fields to column indices


        CSVReader reader = new CSVReader(new FileReader("src/main/java/com/example/ai_demo/LSEG/FLTR.csv"));

        return new CsvToBeanBuilder<DataPoint>(reader)
                .withMappingStrategy(strategy)
                .build()
                .parse();
    }

    public List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }


    public static void addCsv(CSVWriter writer, List<DataPoint> data) {
        for (DataPoint dataItem : data) {
            writer.writeNext(dataItem.toCsvLine());
        }
    }

    public LocalDateTime generateRandomTimestamp(List<DataPoint> dataPoints) {

        LocalDateTime minTimestamp = dataPoints.get(0).getTimestamp();
        LocalDateTime maxTimestamp = dataPoints.get(dataPoints.size() - 1).getTimestamp();

        long minEpoch = minTimestamp.toEpochSecond(ZoneOffset.UTC);
        long maxEpoch = maxTimestamp.toEpochSecond(ZoneOffset.UTC);

        Random random = new Random();
        long randomEpoch = minEpoch + (long) (random.nextDouble() * (maxEpoch - minEpoch));

        return LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC);
    }

    public List<DataPoint> findConsecutiveDataPoints(List<DataPoint> dataPoints, LocalDateTime startTimestamp) {
        // Find the index of the closest data point to the startTimestamp
        int startIndex = findClosestIndex(dataPoints, startTimestamp);

        // Return the next 10 data points
        return dataPoints.subList(startIndex, Math.min(startIndex + 10, dataPoints.size()-1));
    }
///////////////////////////////////

////////////////////////////////////////////////
    private int findClosestIndex(List<DataPoint> dataPoints, LocalDateTime targetTimestamp) {

        TreeMap<LocalDateTime, DataPoint> dataPointsMap = new TreeMap<>();
        for (DataPoint dataPoint : dataPoints) {
            dataPointsMap.put(dataPoint.getTimestamp(), dataPoint);
        }

        Map.Entry<LocalDateTime, DataPoint> floorEntry = dataPointsMap.floorEntry(targetTimestamp);
        Map.Entry<LocalDateTime, DataPoint> ceilingEntry = dataPointsMap.ceilingEntry(targetTimestamp);

        if (floorEntry == null) {
            return dataPointsMap.size() - 1; // Closest is the last element
        }
        if (ceilingEntry == null) {
            return 0; // Closest is the first element
        }

        long floorDiff = Math.abs(floorEntry.getKey().toEpochSecond(ZoneOffset.UTC) - targetTimestamp.toEpochSecond(ZoneOffset.UTC));
        long ceilingDiff = Math.abs(ceilingEntry.getKey().toEpochSecond(ZoneOffset.UTC) - targetTimestamp.toEpochSecond(ZoneOffset.UTC));
       //////////////////////////////////////
        List<DataPoint> valuesList = new ArrayList<>(dataPointsMap.values());

        List<Map.Entry<LocalDateTime, DataPoint>> list = new ArrayList<>(dataPointsMap.entrySet());

        for (LocalDateTime key : dataPointsMap.keySet()) {
            int index = list.indexOf(key);
            System.out.println("Key: " + key + ", Index: " + index);
        }
        return floorDiff <= ceilingDiff ? list.indexOf(floorEntry) : list.indexOf(ceilingEntry);
    }
}
