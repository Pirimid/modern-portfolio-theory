package com.pirimidtech.portfolioservice.model.response;

public class SearchQuoteResponse {

		private String symbol;

		private String name;

		public SearchQuoteResponse() {
		}

		public SearchQuoteResponse(String symbol, String name) {
				this.symbol = symbol;
				this.name = name;
		}

		public String getSymbol() {
				return symbol;
		}

		public void setSymbol(String symbol) {
				this.symbol = symbol;
		}

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}
}
