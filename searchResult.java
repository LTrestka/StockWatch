package com.lucastrestka.stockwatch;

/**
 * Created by trest on 3/1/2018.
 */

public class searchResult {
    private String name;
    private String symbol;
    private String type;


    public searchResult(String name, String symbol, String type) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
