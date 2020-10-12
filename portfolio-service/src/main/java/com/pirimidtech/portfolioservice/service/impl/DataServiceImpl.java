package com.pirimidtech.portfolioservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import com.pirimidtech.portfolioservice.config.YahooFinanceSymbolSuggestionServiceConfig;
import com.pirimidtech.portfolioservice.exception.PortfolioServiceException;
import com.pirimidtech.portfolioservice.model.Index;
import com.pirimidtech.portfolioservice.model.response.SearchQuoteResponse;
import com.pirimidtech.portfolioservice.service.DataService;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service public class DataServiceImpl implements DataService {

		@Autowired private ObjectMapper objectMapper;

		@Autowired private RestTemplate restTemplate;

		@Autowired private YahooFinanceSymbolSuggestionServiceConfig
				yahooFinanceSymbolSuggestionServiceConfig;

		@Autowired @Qualifier("indices") private ConcurrentHashMap<String, Index> indices;

		public DataServiceImpl() {
		}

		public DataServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate,
				YahooFinanceSymbolSuggestionServiceConfig yahooFinanceSymbolSuggestionServiceConfig) {
				this.objectMapper = objectMapper;
				this.restTemplate = restTemplate;
				this.yahooFinanceSymbolSuggestionServiceConfig = yahooFinanceSymbolSuggestionServiceConfig;
		}

		public List<SearchQuoteResponse> searchQuote(String searchTerm, SearchType searchType,
				CountryType countryType, long start, long count) {
				try {
						List<SearchQuoteResponse> quotes = new ArrayList<>();
						switch (searchType) {
								case TICKER: {
										String url = yahooFinanceSymbolSuggestionServiceConfig.getUrl();
										url = UriComponentsBuilder.fromHttpUrl(url).queryParam("query", searchTerm)
												.queryParam("start", start).queryParam("count", count).toUriString();
										String response = this.restTemplate.getForObject(url, String.class);
										JsonNode jsonResponse = this.objectMapper.readTree(response);
										JsonNode jsonFinance = jsonResponse.get("finance");
										Iterator<JsonNode> resultElements = jsonFinance.get("result").elements();
										while (resultElements.hasNext()) {
												Iterator<JsonNode> documentElements =
														resultElements.next().get("documents").elements();
												while (documentElements.hasNext()) {
														JsonNode document = documentElements.next();
														quotes.add(new SearchQuoteResponse(document.path("symbol").asText(),
																document.path("shortName").asText()));
												}
										}
										break;
								}
								case INDEX: {
										this.indices.forEach((key, value) -> {
												if (countryType.equals(value.getCountry())) {
														quotes.add(new SearchQuoteResponse(key, value.getName()));
												}
										});
										break;
								}
						}
						return quotes;
				} catch (JsonProcessingException ex) {
						throw new PortfolioServiceException("Data Service Error");
				}
		}

		public Map<String, Stock> requestStocksBySymbol(boolean includeHistorical, String... symbol) {
				try {
						return YahooFinance.get(symbol, includeHistorical);
				} catch (IOException e) {
						throw new PortfolioServiceException("Data Service Error");
				}
		}

		public Stock requestStocksBySymbol(Calendar from, Interval interval, String symbol) {
				try {
						return YahooFinance.get(symbol, from, interval);
				} catch (IOException e) {
						throw new PortfolioServiceException("Data Service Error");
				}
		}

		public Stock requestStocksBySymbol(Calendar from, Calendar to, Interval interval,
				String symbol) {
				try {
						return YahooFinance.get(symbol, from, to, interval);
				} catch (IOException e) {
						throw new PortfolioServiceException("Data Service Error\n" + e.getMessage());
				}
		}

		public double[] requestIndexBySymbol(Calendar from, Calendar to, Interval interval,
				String symbol) throws JsonProcessingException {
				Index index = this.indices.get(symbol);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(HttpHeaders.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1467.0 Safari/537.36");
				httpHeaders.add("X-Requested-With", "XMLHttpRequest");
				httpHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE);
				httpHeaders.add(HttpHeaders.CONNECTION, "keep-alive");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
				LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("curr_id", index.getId());
				body.add("smlID", 1000000 + (long) (Math.random() * 98999999));
				body.add("header", index.getFullName() + " Historical Data");
				body.add("st_date", simpleDateFormat.format(from.getTime()));
				body.add("end_date", simpleDateFormat.format(to.getTime()));
				body.add("interval_sec", interval);
				body.add("action", "historical_data");

				HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity =
						new HttpEntity<>((body), httpHeaders);

				String responseBody = this.restTemplate
						.exchange("https://www.investing.com/instruments/HistoricalDataAjax", HttpMethod.POST,
								httpEntity, String.class).getBody();
				Elements elements =
						Jsoup.parse(responseBody).getElementById("curr_table").getElementsByTag("tbody").first()
								.getElementsByTag("tr");
				double[] closingDoubles = elements.stream().mapToDouble(element -> Double.parseDouble(
						element.getElementsByTag("td").get(2).attr("data-real-value").replace(",", "")))
						.toArray();
				ArrayUtils.reverse(closingDoubles);
				return closingDoubles;
		}

		public double[] getHistoricalClosings(SearchType searchType, Calendar from, Calendar to,
				Interval interval, String symbol) {
				double[] output = null;
				try {
						switch (searchType) {
								case INDEX: {
										output = requestIndexBySymbol(from, to, interval, symbol);
										break;
								}
								case TICKER: {
										Stock stockDetails = requestStocksBySymbol(from, to, interval, symbol);
										output = stockDetails.getHistory().stream()
												.filter(stockDetail -> stockDetail.getClose() != null)
												.mapToDouble(stockDetail -> Math.abs(stockDetail.getClose().doubleValue()))
												.toArray();
										break;
								}
								default: {
										throw new PortfolioServiceException();
								}
						}
						return output;
				} catch (IOException e) {
						throw new PortfolioServiceException();
				}
		}
}
