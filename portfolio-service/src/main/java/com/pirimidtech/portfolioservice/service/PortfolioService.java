package com.pirimidtech.portfolioservice.service;


import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioAsset;
import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioResult;
import yahoofinance.histquotes.Interval;

import java.util.Calendar;

public interface PortfolioService {
		MarkowitzMaxSharpeRatioResult process(SearchType searchType, CountryType country, Calendar from, Calendar to,
				Interval interval, MarkowitzMaxSharpeRatioAsset... assets);
}
