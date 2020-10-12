package com.pirimidtech.portfolioservice.exception;

public class UndefinedParameterValueException extends Exception {
    public UndefinedParameterValueException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
