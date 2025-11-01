package com.cinelog.CineLog.service;

import com.cinelog.CineLog.dto.*;
import com.cinelog.CineLog.exception.MovieServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
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

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .queryParam("query", searchMovieDto.getQuery())
                    .queryParam("include_adult",searchMovieDto.isInclude_adult())
                    .queryParam("language",searchMovieDto.getLanguage())
                    .queryParam("page",searchMovieDto.getPage());

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
            System.out.println("Calling TMDB API: " + url.replace(apiKey, "***"));

            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }
            addGenresToMovies(response.getResults());
            return response;

        } catch (HttpClientErrorException e) {
            System.err.println("TMDB API Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new MovieServiceException("Failed to search movies: Invalid request", e);
        } catch (HttpServerErrorException e) {
            System.err.println("TMDB API Server Error: " + e.getStatusCode());
            throw new MovieServiceException("TMDB service is temporarily unavailable", e);
        } catch (RestClientException e) {
            System.err.println("Network error while calling TMDB API: " + e.getMessage());
            throw new MovieServiceException("Network error. Please check your connection", e);
        } catch (Exception e) {
            System.err.println("Unexpected error in searchMovie: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Calling TMDB API: " + url.replace(apiKey, "***"));

            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }
            addGenresToMovies(response.getResults());
            return response;
        }
        catch (HttpClientErrorException e) {
            System.err.println("TMDB API Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new MovieServiceException("Failed to fetch trending movies", e);
        } catch (HttpServerErrorException e) {
            System.err.println("TMDB API Server Error: " + e.getStatusCode());
            throw new MovieServiceException("TMDB service is temporarily unavailable", e);
        } catch (RestClientException e) {
            System.err.println("Network error while calling TMDB API: " + e.getMessage());
            throw new MovieServiceException("Network error. Please check your connection", e);
        } catch (Exception e){
            System.err.println("Unexpected error in getTodayTrendingMovies: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Calling TMDB API: " + url.replace(apiKey, "***"));

            MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);

            if (response == null || response.getResults() == null) {
                return new MovieListResponse();
            }

            addGenresToMovies(response.getResults());
            return response;
        }
        catch (HttpClientErrorException e) {
            System.err.println("TMDB API Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new MovieServiceException("Failed to fetch popular movies", e);
        } catch (HttpServerErrorException e) {
            System.err.println("TMDB API Server Error: " + e.getStatusCode());
            throw new MovieServiceException("TMDB service is temporarily unavailable", e);
        } catch (RestClientException e) {
            System.err.println("Network error while calling TMDB API: " + e.getMessage());
            throw new MovieServiceException("Network error. Please check your connection", e);
        } catch (Exception e){
            System.err.println("Unexpected error in getPopularMovies: " + e.getMessage());
            e.printStackTrace();
            throw new MovieServiceException("Can't find popular movies", e);
        }
    }

    public MovieDetailDto getDeepMovieDetails(Integer movie_id, MovieQueryDto movieQueryDto){
        try {
            if (movie_id == null || movie_id <= 0) {
                throw new MovieServiceException("Invalid movie ID");
            }

            String base = "https://api.themoviedb.org/3/movie/{movie_id}";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .queryParam("language", movieQueryDto.getLanguage());

            if (movieQueryDto.getAppend_to_response() != null) {
                builder.queryParam("append_to_response", movieQueryDto.getAppend_to_response());
            }

            String url = builder.buildAndExpand(movie_id).toUriString();
            System.out.println("Calling TMDB API for movie details: " + url.replace(apiKey, "***"));

            MovieDetailDto response = restTemplate.getForObject(url, MovieDetailDto.class);

            if (response == null) {
                System.err.println("Received null response from TMDB API for movie ID: " + movie_id);
                throw new MovieServiceException("Movie not found");
            }

            return response;

        } catch (HttpClientErrorException e) {
            System.err.println("TMDB API Client Error for movie " + movie_id + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new MovieServiceException("Movie not found", e);
            }
            throw new MovieServiceException("Failed to fetch movie details", e);

        } catch (HttpServerErrorException e) {
            System.err.println("TMDB API Server Error for movie " + movie_id + ": " + e.getStatusCode());
            throw new MovieServiceException("TMDB service is temporarily unavailable", e);

        } catch (RestClientException e) {
            System.err.println("Network error while fetching movie " + movie_id + ": " + e.getMessage());
            throw new MovieServiceException("Network error. Please check your connection", e);

        } catch (MovieServiceException e) {
            throw e;

        } catch (Exception e) {
            System.err.println("Unexpected error in getDeepMovieDetails for movie " + movie_id + ": " + e.getMessage());
            e.printStackTrace();
            throw new MovieServiceException("Failed to fetch movie details", e);
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
    public void getGenreMap(){
        try {
            String base = "https://api.themoviedb.org/3/genre/movie/list";
            String url = UriComponentsBuilder.fromHttpUrl(base)
                    .queryParam("api_key", apiKey)
                    .toUriString();

            System.out.println("Initializing genre map from TMDB API");

            GenreResponseDto dto = restTemplate.getForObject(url, GenreResponseDto.class);

            if (dto == null || dto.getGenres() == null) {
                System.err.println("Failed to fetch genres from TMDB API");
                return;
            }

            List<GenreDto> dtoList = dto.getGenres();
            for (GenreDto map : dtoList) {
                hashMap.put(map.getId(), map.getName());
            }

            System.out.println("Successfully loaded " + hashMap.size() + " genres");

        } catch (Exception e){
            System.err.println("Error fetching genre list from TMDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
