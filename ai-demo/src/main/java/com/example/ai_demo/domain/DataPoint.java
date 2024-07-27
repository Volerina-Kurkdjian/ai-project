package com.example.ai_demo.domain;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;


@RequiredArgsConstructor
public class DataPoint {

    private String StockID;
    private LocalDateTime timestamp;
    private Long priceValue;



    public Long getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(Long priceValue) {
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

}
