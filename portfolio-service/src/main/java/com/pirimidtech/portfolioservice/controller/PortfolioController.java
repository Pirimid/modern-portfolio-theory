package com.pirimidtech.portfolioservice.controller;

import com.pirimidtech.portfolioservice.model.MarkowitzMaxSharpeRatioRequest;
import com.pirimidtech.portfolioservice.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yahoofinance.histquotes.Interval;

@RestController @RequestMapping(path = "portfolio") public class PortfolioController {

		@Autowired private PortfolioService portfolioService;

		public PortfolioController(PortfolioService portfolioService) {
				this.portfolioService = portfolioService;
		}

		@PostMapping(path = "/optimize")
		public ResponseEntity optimize(
				@RequestBody MarkowitzMaxSharpeRatioRequest markowitzMaxSharpeRatioRequest) {
				return ResponseEntity.ok(this.portfolioService
						.process(markowitzMaxSharpeRatioRequest.getSearchType(),
								markowitzMaxSharpeRatioRequest.getCountry(),
								markowitzMaxSharpeRatioRequest.getFrom(), markowitzMaxSharpeRatioRequest.getTo(),
								Interval.DAILY, markowitzMaxSharpeRatioRequest.getMarkowitzMaxSharpeRatioAssets()));
		}

}
