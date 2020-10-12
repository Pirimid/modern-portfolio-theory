package com.pirimidtech.portfolioservice.service.impl;

import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioAsset;
import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioResult;
import com.pirimidtech.portfolioservice.service.DataService;
import com.pirimidtech.portfolioservice.service.PortfolioService;
import com.pirimidtech.portfolioservice.util.Constant;
import com.pirimidtech.portfolioservice.util.portfolio.EfficientFrontier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yahoofinance.histquotes.Interval;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service public class PortfolioServiceImpl implements PortfolioService {

		@Autowired private DataService dataService;

		public PortfolioServiceImpl(DataService dataService) {
				this.dataService = dataService;
		}

		public MarkowitzMaxSharpeRatioResult process(SearchType searchType, CountryType country,
				Calendar from, Calendar to, Interval interval, MarkowitzMaxSharpeRatioAsset... assets) {

				double riskFreeRate = 0.55;

								switch (country) {
										case IN: {
												riskFreeRate = 0.6;
												break;
										}
										case US: {
												riskFreeRate = 0.2;
												break;
										}
								}

				Map<String, double[]> data = new HashMap<>();
				String[] symbols = new String[assets.length];

				double[] upper = new double[assets.length];
				double[] lower = new double[assets.length];
				double[] init = new double[assets.length];

				Arrays.fill(init, 0.05);

				for (int i = 0; i < assets.length; i++) {
						upper[i] = assets[i].getMaxWeight();
						lower[i] = assets[i].getMinWeight();
						symbols[i] = assets[i].getSymbol();
						if (init[i] < lower[i]) {
								init[i] = lower[i];
						} else if (init[i] > upper[i]) {
								init[i] = upper[i];
						}
				}

				long datasetSize = Constant.MAX_DATASET_SIZE;

				for (String symbol : symbols) {
						double[] closings =
								this.dataService.getHistoricalClosings(searchType, from, to, interval, symbol);
						if (closings.length < datasetSize) {
								datasetSize = closings.length;
						}
						data.put(symbol, closings);
				}

				for (String key : data.keySet()) {
						if (data.get(key).length != datasetSize) {
								data.put(key, Arrays.stream(data.get(key)).limit(datasetSize).toArray());
						}
				}

				EfficientFrontier ef = new EfficientFrontier(data, riskFreeRate, 12);

				return ef.getMaxSharpeRatio(upper, lower, init);
		}
}

