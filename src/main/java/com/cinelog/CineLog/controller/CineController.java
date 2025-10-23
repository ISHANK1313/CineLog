package com.cinelog.CineLog.controller;

import com.cinelog.CineLog.dto.*;
import com.cinelog.CineLog.exception.MovieServiceException;
import com.cinelog.CineLog.service.CineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static javax.print.attribute.standard.ReferenceUriSchemesSupported.HTTP;

@RestController
@RequestMapping("/api")
public class CineController {
    @Autowired
    private CineService service;
    @GetMapping("/movies")
    public ResponseEntity<?> getMovieDetails(@ModelAttribute SearchMovieDto searchMovieDto){
        try {
            return ResponseEntity.ok(service.searchMovie(searchMovieDto));
        } catch (MovieServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingTodayMovies(@ModelAttribute TodayTrendingDto todayTrendingDto){
        try{
            return ResponseEntity.ok(service.getTodayTrendingMovies(todayTrendingDto));
        }
        catch (MovieServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error : "+e.getMessage());
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularMovies(@ModelAttribute PopularMovieDto popularMovieDto){
        try{
            return ResponseEntity.ok(service.getPopularMovies(popularMovieDto));
        }
        catch (MovieServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error : "+e.getMessage());
        }
    }
    public ResponseEntity<?> getDeepMovieDetails(@RequestParam Integer movie_id, @ModelAttribute MovieQueryDto movieQueryDto){
        try{
            return ResponseEntity.ok(service.getDeepMovieDetails(movie_id, movieQueryDto));
        }
        catch (MovieServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error : "+e.getMessage());
        }
    }
}
