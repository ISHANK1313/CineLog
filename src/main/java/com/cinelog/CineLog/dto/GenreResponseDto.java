package com.cinelog.CineLog.dto;

import java.util.List;

public class GenreResponseDto {
    private List<GenreDto> genres;

    public List<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }
}
