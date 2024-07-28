package com.example.ai_demo.dto;


import lombok.Getter;

public class StockExchangeOption {

    private String stockExchange;
    private Integer numberOfFiles;



    public StockExchangeOption(String stockExchange, Integer numberOfFiles) {
        this.stockExchange = stockExchange;
        this.numberOfFiles = numberOfFiles;
    }


    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }
}
