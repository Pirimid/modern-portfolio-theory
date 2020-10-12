package com.pirimidtech.portfolioservice.util.data;

import java.util.*;

public class DataProcessor {

    public static double[][] dropna(double[][] in) {
        Set<Long> skipLine = new HashSet<>();

        for (double[] doubles : in) {
            for (int j = 0; j < doubles.length; j++) {
                if (Double.isNaN(doubles[j])) {
                    skipLine.add(Long.valueOf(j));
                }
            }
        }

        double[][] ret = new double[in.length][];

        for (int i = 0 ; i < in.length; i++) {
            List<Double> tmp = new LinkedList<>();
            for(int j = 0 ; j < in[i].length ; j++ ) {
                if(!skipLine.contains(Long.valueOf(j))) {
                    tmp.add(in[i][j]);
                }
            }
            ret[i] = tmp.stream().mapToDouble(Double::doubleValue).toArray();
        }
        return ret;
    }
}
