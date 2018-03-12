package com.lucastrestka.stockwatch;

/**
 * Created by trest on 2/18/2018.
 */

public class Stock {
    private String NASDAQ;
    private String company_Name;
    private String current_Price;
    private String difference;
    private String changePercent;
    private String arrow;

    public Stock(){
        this.NASDAQ = "";
        this.company_Name = "";
        this.current_Price = "";
        this.difference = "";
        this.changePercent = "";
        this.arrow = "";
    }


    public Stock(String NASDAQ, String company_Name, String current_Price, String difference, String changePercent, String arrow) {
        this.NASDAQ = NASDAQ;
        this.company_Name = company_Name;
        this.current_Price = current_Price;
        this.difference = difference;
        this.changePercent = changePercent;
        this.arrow = arrow;
    }


    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getNASDAQ() {
        return NASDAQ;
    }

    public void setNASDAQ(String NASDAQ) {
        this.NASDAQ = NASDAQ;
    }

    public String getCompanyName() {
        return company_Name;
    }

    public void setCompanyName(String company_Name) {
        this.company_Name = company_Name;
    }

    public String getCurrentPrice() {
        return current_Price;
    }

    public void setCurrentPrice(String current_Price) {
        this.current_Price = current_Price;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public String getArrow() {
        return arrow;
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }

}
