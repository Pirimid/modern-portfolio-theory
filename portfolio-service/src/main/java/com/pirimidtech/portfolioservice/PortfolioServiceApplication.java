package com.pirimidtech.portfolioservice;

import com.opencsv.CSVReader;
import com.pirimidtech.portfolioservice.model.Index;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication public class PortfolioServiceApplication {



		public static void main(String[] args) {
				SpringApplication.run(PortfolioServiceApplication.class, args);
		}

		@Bean("indices") public ConcurrentHashMap<String, Index> indices()
				throws FileNotFoundException {
				ConcurrentHashMap<String, Index> concurrentHashMap = new ConcurrentHashMap<>();
				CSVReader csvReader = new CSVReader(
						new FileReader(getClass().getClassLoader().getResource("indices.csv").getPath()));
				Iterator<String[]> dataElements = csvReader.iterator();
				while (dataElements.hasNext()) {
						String[] row = dataElements.next();
						String country = row[0];
						if(country.equals("IN") || country.equals("US")) {
								String name = row[1];
								String fullName = row[2];
								String tag = row[3];
								long id = Long.parseLong(row[4]);
								String symbol = row[5];
								String market = row[6];
								concurrentHashMap.put(symbol, new Index(Enum.valueOf(CountryType.class, country), name, fullName, tag, id, symbol, market));
						}
				}
				return concurrentHashMap;
		}

		@Bean("countries") public ConcurrentHashMap<CountryType, String> countries()
				throws FileNotFoundException {
				ConcurrentHashMap<CountryType, String> concurrentHashMap = new ConcurrentHashMap<>();
				concurrentHashMap.put(CountryType.IN, "india");
				concurrentHashMap.put(CountryType.US, "united states");
				return concurrentHashMap;
		}

}
