package com.pirimidtech.portfolioservice.util.portfolio;

import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioResult;
import com.pirimidtech.portfolioservice.util.Constant;
import com.pirimidtech.portfolioservice.util.data.DataProcessor;
import com.pirimidtech.portfolioservice.util.math.cobyla.Calcfc;
import com.pirimidtech.portfolioservice.util.math.cobyla.Cobyla;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class EfficientFrontier {
    private double[][] cov;
    private double[] mean;
    private double[][] returns;
    static final private Mean m = new Mean();
    private  Map<String, double[]> data;
    private  String[] symbols;
    private  double riskFreeRate;

    public EfficientFrontier(Map<String, double[]> data, double riskFreeRate, int frequency) {
        this.symbols = data.keySet().toArray(new String[0]);
        this.riskFreeRate = riskFreeRate;
        this.data = data;
        returns = getReturns();
        mean = getMeanReturn(returns, frequency);
        cov = getCovariance(returns);
    }

    private MarkowitzMaxSharpeRatioResult getResult(double[]mean, double[][] cov, double[] bestWeight) {
        double weightedReturns = getWeightedReturn(bestWeight, mean);
        double bestSharpeRatio = getWeightedSharpeRatio(bestWeight, mean, cov, riskFreeRate);
        double variance = Math.pow(((weightedReturns - riskFreeRate) / bestSharpeRatio), 2);
        return new MarkowitzMaxSharpeRatioResult(symbols, bestWeight, bestSharpeRatio, weightedReturns, variance, returns, mean);
    }

    public MarkowitzMaxSharpeRatioResult getMaxSharpeRatio(double[] upperBound, double[] lowerBound, double[] initGuess) {
        Cobyla.FindMinimum(getObjectiveFunction(mean,cov,lowerBound, upperBound), initGuess.length, initGuess.length * 2, initGuess, 0.5, 1.0e-6, 0, Constant.MAXTRY);
        return getResult(mean, cov, normalizeWeight(initGuess));
    }

    final protected double[][] getReturns() {
        double[][] returns = new double[symbols.length][];
        for(int i = 0 ; i < symbols.length ; i++) {
            double[] tmp = data.get(symbols[i]);
            double[] ret = new double[tmp.length - 1];
            for(int j = 1 ; j < tmp.length ; j++) {
                ret[j - 1] = (tmp[j] / tmp[j - 1] - 1) * 100;
            }
            returns[i] = ret;
        }
        return DataProcessor.dropna(returns);
    }

    final protected double[] getMeanReturn(double[][] returns, int frequency) {
        double[] mean = new double[returns.length];
        for(int i = 0 ; i < returns.length; i++) {
            mean[i] = m.evaluate(returns[i], 0, returns[i].length) * frequency;
        }
        return mean;
    }

    final protected double[][] getCovariance(double[][] data) {
        RealMatrix matrix = MatrixUtils.createRealMatrix(data).transpose();
        Covariance covariance = new Covariance(matrix,false);
        return covariance.getCovarianceMatrix().getData();

    }

    final protected double getWeightedReturn(double[] weight, double[] returns) {
        double ret = 0;
        for(int i = 0 ; i < weight.length; i++ ) {
            ret += weight[i] * returns[i];
        }
        return ret;
    }

    final protected double getPortfolioVariance(double[][] cov, double[] weight) {
        RealMatrix w = new Array2DRowRealMatrix(weight);
        RealMatrix r = new Array2DRowRealMatrix(cov);
        return w.transpose().multiply(r).multiply(w).getData()[0][0];
    }

    public double getWeightedSharpeRatio(double[] weight, double[] mean, double[][] cov, double riskFreeRate) {
        double varP = getPortfolioVariance(cov, weight);
        double weightMean = getWeightedReturn(weight, mean);
        return ((weightMean - riskFreeRate) / Math.sqrt(varP));
    }

    protected Calcfc getObjectiveFunction(double[] mean, double[][] cov, double [] lowerBound, double [] upperBound) {
        class MaxSharpeRatio implements Calcfc, Serializable {
            @Override
            public double Compute(int n, int m, double[] weight, double[] con) {
                int i = 0;
                weight = normalizeWeight(weight);
                for (int i1 = 0; i1 < weight.length; i1++) {
                    con[i] = weight[i1] - lowerBound[i1];
                    i++;
                    con[i] = upperBound[i1] - weight[i1];
                    i++;
                }
                return 1/getWeightedSharpeRatio(weight, mean, cov, riskFreeRate);
            }
        }
        return new MaxSharpeRatio();
    }

    protected double[] normalizeWeight(double[] w) {
        double sum = Arrays.stream(w).map(Math::abs).sum();
        return Arrays.stream(w).map(e -> (Math.abs(e)) / sum).toArray();
    }

    protected BOBYQAOptimizer getBOBYQAOptimizer(int dim) {
        final int numInterpolationPoints = 2 * dim + 1;
        return new BOBYQAOptimizer(numInterpolationPoints);
    }

    protected double[] BOBYQAOptimize(double[] upperBound, double[] lowerBound, double[] initGuess, MultivariateFunction fun, GoalType goal) {
        BOBYQAOptimizer optimizer = getBOBYQAOptimizer(initGuess.length);
        MaxEval maxEval = new MaxEval(Constant.MAXTRY);
        SimpleBounds bounds = new SimpleBounds(lowerBound, upperBound);
        PointValuePair solution = optimizer.optimize(
                new InitialGuess(initGuess),
                new LinearConstraintSet(new LinearConstraint(new double[]{1,0,0}, Relationship.EQ, 2)),
                new org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction(fun),
                goal,
                bounds,
                maxEval
        );
        return normalizeWeight(solution.getPoint());
    }

    protected MultivariateFunction getObjectiveFunction(double[] mean, double[][] cov) {
        class MaxSharpeRatio implements MultivariateFunction, Serializable {
            @Override
            public double value(double[] weight) {
                weight = normalizeWeight(weight);
                return getWeightedSharpeRatio(weight, mean, cov, riskFreeRate);
            }
        }
        return new MaxSharpeRatio();
    }
}