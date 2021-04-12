package com.glroland.myeda.manualeda;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord( separator = "," )
public class HistoricQuote {

    private String symbol;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DataField(pos = 1, position = 1, pattern="yyyy-MM-dd")
    private Date date;

    @DataField(pos = 2, position = 2)
    private double openPrice;

    @DataField(pos = 3, position = 3)
    private double dailyHighPrice;

    @DataField(pos = 4, position = 4)
    private double dailyLowPrice;

    @DataField(pos = 5, position = 5)
    private double closingPrice;

    @DataField(pos = 6, position = 6)
    private double adjustedClosingPrice;

    @DataField(pos = 7, position = 7)
    private long dailyVolume;

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public double getOpenPrice() {
        return openPrice;
    }
    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }
    public double getDailyHighPrice() {
        return dailyHighPrice;
    }
    public void setDailyHighPrice(double dailyHighPrice) {
        this.dailyHighPrice = dailyHighPrice;
    }
    public double getDailyLowPrice() {
        return dailyLowPrice;
    }
    public void setDailyLowPrice(double dailyLowPrice) {
        this.dailyLowPrice = dailyLowPrice;
    }
    public double getClosingPrice() {
        return closingPrice;
    }
    public void setClosingPrice(double closingPrice) {
        this.closingPrice = closingPrice;
    }
    public long getDailyVolume() {
        return dailyVolume;
    }
    public void setDailyVolume(long dailyVolume) {
        this.dailyVolume = dailyVolume;
    }

    public double getAdjustedClosingPrice() {
        return adjustedClosingPrice;
    }
    public void setAdjustedClosingPrice(double adjustedClosingPrice) {
        this.adjustedClosingPrice = adjustedClosingPrice;
    }

    
}
