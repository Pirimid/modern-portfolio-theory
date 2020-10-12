package com.pirimidtech.portfolioservice.controller;

import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import com.pirimidtech.portfolioservice.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping(path = "data") public class DataController {

	@Autowired private DataService dataService;

	public DataController() {
	}

	public DataController(DataService dataService) {
		this.dataService = dataService;
	}

	@GetMapping(path = "search/quote", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity searchQuote(@RequestParam String searchTerm,
		@RequestParam SearchType searchType, @RequestParam CountryType country) {

		return ResponseEntity
			.ok(this.dataService.searchQuote(searchTerm, searchType, country, 0, 300));

	}

	@GetMapping(path = "quote", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getQuote(@RequestParam String symbol,
		@RequestParam(defaultValue = "false") boolean includeHistorical) {
		return ResponseEntity.ok(this.dataService.requestStocksBySymbol(includeHistorical, symbol));
	}

	@PostMapping(path = "quotes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getQuotes(@RequestBody String[] symbols,
		@RequestParam(defaultValue = "false") boolean includeHistorical) {
		return ResponseEntity
			.ok(this.dataService.requestStocksBySymbol(includeHistorical, symbols));
	}
}
