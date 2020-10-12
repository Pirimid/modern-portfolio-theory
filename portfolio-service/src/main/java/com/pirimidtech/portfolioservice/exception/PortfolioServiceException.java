package com.pirimidtech.portfolioservice.exception;


public class PortfolioServiceException extends RuntimeException {

    public PortfolioServiceException() {
        super();
    };

    public PortfolioServiceException(String message) {
        super(message);
    }
}
