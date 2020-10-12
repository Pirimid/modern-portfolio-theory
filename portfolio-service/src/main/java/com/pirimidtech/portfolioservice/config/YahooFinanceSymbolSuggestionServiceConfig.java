package com.pirimidtech.portfolioservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.dataservice.config.yahooapis.symbolsuggestionservice")
public class YahooFinanceSymbolSuggestionServiceConfig {

    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
