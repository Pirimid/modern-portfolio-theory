package com.pirimidtech.portfolioservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import com.pirimidtech.portfolioservice.model.response.SearchQuoteResponse;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface DataService {
		Map<String, Stock> requestStocksBySymbol(boolean includeHistorical, String... symbol);

		List<SearchQuoteResponse> searchQuote(String searchTerm, SearchType searchType,
				CountryType country, long start, long count);

		Stock requestStocksBySymbol(Calendar from, Interval interval, String symbol);

		Stock requestStocksBySymbol(Calendar from, Calendar to, Interval interval, String symbol);

		double[] getHistoricalClosings(SearchType searchType, Calendar from, Calendar to,
				Interval interval, String symbol);

		double[] requestIndexBySymbol(Calendar from, Calendar to, Interval interval, String symbol)
				throws JsonProcessingException;
}
