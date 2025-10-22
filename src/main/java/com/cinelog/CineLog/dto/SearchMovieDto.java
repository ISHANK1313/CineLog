package com.cinelog.CineLog.dto;

public class SearchMovieDto {
    private String query;
    private boolean include_adult=false;
    private String language="en-US";
    private String primary_release_year;
    private Integer page=1;
    private String region;
    private String Year;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isInclude_adult() {
        return include_adult;
    }

    public void setInclude_adult(boolean include_adult) {
        this.include_adult = include_adult;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrimary_release_year() {
        return primary_release_year;
    }

    public void setPrimary_release_year(String primary_release_year) {
        this.primary_release_year = primary_release_year;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }
}
