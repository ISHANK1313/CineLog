package com.cinelog.CineLog.service;

import com.cinelog.CineLog.dto.*;
import com.cinelog.CineLog.exception.MovieServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class CineService {
    @Value("${tmdb.api.key}")
    private  String  apiKey;
    @Autowired
    private RestTemplate restTemplate;

    private HashMap<Integer,String>hashMap= new HashMap<>();

    public MovieListResponse searchMovie(SearchMovieDto searchMovieDto) {
        try {
            String base = "https://api.themoviedb.org/3/search/movie";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base).queryParam("api_key", apiKey)
                    .queryParam("query", searchMovieDto.getQuery())
                    .queryParam("include_adult",searchMovieDto.isInclude_adult())
                    .queryParam("language",searchMovieDto.getLanguage())
                    .queryParam("page",searchMovieDto
                            .getPage());

            if(searchMovieDto.getPrimary_release_year()!=null){
                builder.queryParam("primary_release_year",searchMovieDto.getPrimary_release_year());
            }
            if(searchMovieDto.getRegion()!=null){
                builder.queryParam("region",searchMovieDto.getRegion());
            }
            if(searchMovieDto.getYear()!=null){
                builder.queryParam("year",searchMovieDto.getYear());
            }
            String url= builder.toUriString();
            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }
            addGenresToMovies(response.getResults());
            return response;

        } catch (Exception e) {
            throw new MovieServiceException("Failed to search movies: " + e.getMessage(), e);
        }

    }

    public MovieListResponse getTodayTrendingMovies(TodayTrendingDto todayTrendingDto){
        try {
            String base = "https://api.themoviedb.org/3/trending/movie/day";
            UriComponentsBuilder builder=UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .queryParam("language",todayTrendingDto.getLanguage());

            String url = builder.toUriString();
            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }
            addGenresToMovies(response.getResults());
            return response;
        }
        catch (Exception e){
            throw new MovieServiceException("Can't find trending Movies...",e);
        }

    }

    public MovieListResponse getPopularMovies(PopularMovieDto popularMovieDto){
        try {

            if (popularMovieDto.getPage() != null && popularMovieDto.getPage() > 500) {

                popularMovieDto.setPage(500);
            }

            String base = "https://api.themoviedb.org/3/movie/popular";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .queryParam("language", popularMovieDto.getLanguage())
                    .queryParam("page", popularMovieDto.getPage());

            if(popularMovieDto.getRegion() != null){
                builder.queryParam("region", popularMovieDto.getRegion());
            }

            String url = builder.toUriString();
            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);

            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }

            addGenresToMovies(response.getResults());
            return response;
        }
        catch (Exception e){
            throw new MovieServiceException("Can't find popular movies", e);
        }
    }

    public MovieDetailDto getDeepMovieDetails(Integer movie_id,MovieQueryDto movieQueryDto){
        try {
            String base = "https://api.themoviedb.org/3/movie/{movie_id}";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .queryParam("language", movieQueryDto.getLanguage());
            if (movieQueryDto.getAppend_to_response() != null) {
                builder.queryParam("append_to_response",movieQueryDto.getAppend_to_response());
            }
            String url = builder.buildAndExpand(movie_id).toUriString();
            MovieDetailDto response = restTemplate.getForObject(url, MovieDetailDto.class);
            if (response == null) {
                return new MovieDetailDto();
            }

            return response;
        } catch (Exception e) {
            throw new MovieServiceException("can't search for movie details try again",e);
        }

    }


    private void addGenresToMovies(List<MovieDto> movies) {
        if(movies == null) return;

        for(MovieDto movie : movies) {
            List<String> genres = new ArrayList<>();
            if(movie.getGenre_ids() != null) {
                for(Integer genreId : movie.getGenre_ids()) {
                    String genreName = hashMap.get(genreId);
                    if(genreName != null) {
                        genres.add(genreName);
                    }
                }
            }
            movie.setGenres(genres);
        }
    }

    @PostConstruct
    public void  getGenreMap(){
       try {
           String base = "https://api.themoviedb.org/3/genre/movie/list";
           String url = UriComponentsBuilder.fromHttpUrl(base).queryParam("api_key", apiKey)
                   .toUriString();
           GenreResponseDto dto = restTemplate.getForObject(url, GenreResponseDto.class);
           List<GenreDto> dtoList = dto.getGenres();
           for (int i = 0; i < dtoList.size(); i++) {
               GenreDto map = dtoList.get(i);
               hashMap.put(map.getId(), map.getName());

           }
       }
       catch (Exception e){
           e.printStackTrace();
           System.out.println("something went wrong...");
       }

    }


}
