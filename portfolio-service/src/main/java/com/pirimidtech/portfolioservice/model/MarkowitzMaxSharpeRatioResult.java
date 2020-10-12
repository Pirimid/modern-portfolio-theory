package com.pirimidtech.portfolioservice.model;

import com.pirimidtech.portfolioservice.exception.PortfolioServiceException;

import java.util.HashMap;
import java.util.Map;

public class MarkowitzMaxSharpeRatioResult {

    private class MarkowitzMaxSharpeRatioResultAsset {
        private double bestWeight;
        private double [] returns;
        private double meanReturn;

        public MarkowitzMaxSharpeRatioResultAsset(double bestWeight, double[] returns, double meanReturn) {
            this.bestWeight = bestWeight;
            this.returns = returns;
            this.meanReturn = meanReturn;
        }

        public double getBestWeight() {
            return bestWeight;
        }

        public double[] getReturns() {
            return returns;
        }

        public double getMeanReturn() {
            return meanReturn;
        }
    }
    private Map<String, MarkowitzMaxSharpeRatioResultAsset> assetData;
    private double sharpeRatio;
    private double weightedReturns;
    private double portfolioVariance;

    public double getWeightedReturns() {
        return weightedReturns;
    }

    public MarkowitzMaxSharpeRatioResult(String[] symbols, double[] weight, double sharpeRatio, double weightedReturns, double portfolioVariance, double [][] returns, double [] meanReturns) {
        if(symbols.length != weight.length) {
            throw new PortfolioServiceException("symbols length != weight length");
        }
        assetData = new HashMap<>();

        for(int i = 0 ; i < symbols.length; i++ ) {
            assetData.put(symbols[i], new MarkowitzMaxSharpeRatioResultAsset(weight[i], returns[i],meanReturns[i]));
        }

        this.sharpeRatio = sharpeRatio;
        this.weightedReturns = weightedReturns;
        this.portfolioVariance = portfolioVariance;
    }

    public Map<String, MarkowitzMaxSharpeRatioResultAsset> getAssetData() {
        return assetData;
    }

    public void setAssetData(Map<String, MarkowitzMaxSharpeRatioResultAsset> assetData) {
        this.assetData = assetData;
    }

    public double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public void setWeightedReturns(double weightedReturns) {
        this.weightedReturns = weightedReturns;
    }

    public double getPortfolioVariance() {
        return portfolioVariance;
    }

    public void setPortfolioVariance(double portfolioVariance) {
        this.portfolioVariance = portfolioVariance;
    }
}
