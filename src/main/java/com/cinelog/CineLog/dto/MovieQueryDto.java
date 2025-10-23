package com.cinelog.CineLog.dto;

public class MovieQueryDto {
    private String append_to_response;
    private String language="en-US";

    public String getAppend_to_response() {
        return append_to_response;
    }

    public void setAppend_to_response(String append_to_response) {
        this.append_to_response = append_to_response;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
