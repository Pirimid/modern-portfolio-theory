package com.pirimidtech.portfolioservice.model;

import com.pirimidtech.portfolioservice.CountryType;
import com.pirimidtech.portfolioservice.SearchType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;

public class MarkowitzMaxSharpeRatioRequest {

		private SearchType searchType;

		private CountryType country;

		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private Calendar from;

		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private Calendar to;

		private MarkowitzMaxSharpeRatioAsset[] markowitzMaxSharpeRatioAssets;

		public MarkowitzMaxSharpeRatioRequest() {
		}

		public MarkowitzMaxSharpeRatioRequest(SearchType searchType, CountryType country, Calendar from,
				Calendar to, MarkowitzMaxSharpeRatioAsset[] markowitzMaxSharpeRatioAssets) {
				this.searchType = searchType;
				this.from = from;
				this.to = to;
				this.country = country;
				this.markowitzMaxSharpeRatioAssets = markowitzMaxSharpeRatioAssets;
		}

		public CountryType getCountry() {
				return country;
		}

		public void setCountry(CountryType country) {
				this.country = country;
		}

		public SearchType getSearchType() {
				return searchType;
		}

		public void setSearchType(SearchType searchType) {
				this.searchType = searchType;
		}

		public Calendar getFrom() {
				return from;
		}

		public void setFrom(Calendar from) {
				this.from = from;
		}

		public Calendar getTo() {
				return to;
		}

		public void setTo(Calendar to) {
				this.to = to;
		}

		public MarkowitzMaxSharpeRatioAsset[] getMarkowitzMaxSharpeRatioAssets() {
				return markowitzMaxSharpeRatioAssets;
		}

		public void setMarkowitzMaxSharpeRatioAssets(
				MarkowitzMaxSharpeRatioAsset[] markowitzMaxSharpeRatioAssets) {
				this.markowitzMaxSharpeRatioAssets = markowitzMaxSharpeRatioAssets;
		}
}
