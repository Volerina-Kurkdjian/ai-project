package com.example.ai_demo.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RequiredArgsConstructor
public class DataPoint {

    private String StockID;
    private LocalDateTime timestamp;
    private Float priceValue;

    static private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public DataPoint(String[] data) {
        StockID = data[0];
        this.timestamp = LocalDate.parse(data[1], dateFormat).atStartOfDay();
        this.priceValue = Float.parseFloat(data[2]);
    }

//    public DataPoint(String stockID, LocalDateTime timestamp, Long priceValue) {
//        StockID = stockID;
//        this.timestamp = timestamp;
//        this.priceValue = priceValue;
//    }

    public Float getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(Float priceValue) {
        this.priceValue = priceValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStockID() {
        return StockID;
    }

    public void setStockID(String stockID) {
        this.StockID = stockID;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "name='" + priceValue + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + StockID +
                '}';
    }

    public String[] toCsvLine() {
        return new String[]{
            StockID,
            timestamp.format(dateFormat),
            priceValue.toString()
        };

    }

}
