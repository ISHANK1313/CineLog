package com.cinelog.CineLog.exception;

public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException(String message) {
        super(message);
    }
    public TokenNotValidException(String message,Throwable cause){
      super(message, cause);
    }
}
