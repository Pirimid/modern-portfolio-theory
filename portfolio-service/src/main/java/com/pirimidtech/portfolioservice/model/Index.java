package com.pirimidtech.portfolioservice.model;

import com.pirimidtech.portfolioservice.CountryType;

public class Index {

		private CountryType country;

		private String name;

		private String fullName;

		private String tag;

		private long id;

		private String symbol;

		private String market;

		public Index(CountryType country, String name, String fullName, String tag, long id, String symbol,
				String market) {
				this.country = country;
				this.name = name;
				this.fullName = fullName;
				this.tag = tag;
				this.id = id;
				this.symbol = symbol;
				this.market = market;
		}

		public CountryType getCountry() {
				return country;
		}

		public void setCountry(CountryType country) {
				this.country = country;
		}

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}

		public String getFullName() {
				return fullName;
		}

		public void setFullName(String fullName) {
				this.fullName = fullName;
		}

		public String getTag() {
				return tag;
		}

		public void setTag(String tag) {
				this.tag = tag;
		}

		public long getId() {
				return id;
		}

		public void setId(long id) {
				this.id = id;
		}

		public String getSymbol() {
				return symbol;
		}

		public void setSymbol(String symbol) {
				this.symbol = symbol;
		}

		public String getMarket() {
				return market;
		}

		public void setMarket(String market) {
				this.market = market;
		}
}
