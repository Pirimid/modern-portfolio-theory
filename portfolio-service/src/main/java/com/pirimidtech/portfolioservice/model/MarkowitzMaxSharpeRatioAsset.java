package com.pirimidtech.portfolioservice.model;

public class MarkowitzMaxSharpeRatioAsset {

    private String symbol;

    private double minWeight = 0.0d;

    private double maxWeight = 1.0d;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(double minWeight) {
        this.minWeight = minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }
}
