package com.cinelog.CineLog.controller;

import com.cinelog.CineLog.dto.AddToWatchList;
import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.entity.WatchlistItem;
import com.cinelog.CineLog.repository.WatchlistItemRepo;
import com.cinelog.CineLog.service.UserService;
import com.cinelog.CineLog.service.WatchListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/watchlist")
public class WatchListController {
    @Autowired
    private WatchListService watchListService;
    @Autowired
    private UserService userService;
    @Autowired
    private WatchlistItemRepo watchlistItemRepo;

    @GetMapping
    public ResponseEntity<?> getAllWatchList(){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();
            User user = userService.findUserByEmail(email);
            List<WatchlistItem> watchlistItemList = watchListService.findByUser(user);
            if (watchlistItemList == null) {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
            return ResponseEntity.ok().body(watchlistItemList);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving watchlist: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> addMovieInWatchList(@Valid @RequestBody AddToWatchList addToWatchList){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();
            User user = userService.findUserByEmail(email);

            Optional<WatchlistItem> existing = watchlistItemRepo
                    .findByUserAndTmdbMovieId(user, addToWatchList.getTmdbMovieId());

            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Movie already exists in your watchlist");
            }

            WatchlistItem watchlistItem = new WatchlistItem();
            watchlistItem.setUser(user);
            watchlistItem.setAddedAt(LocalDateTime.now());
            watchlistItem.setTmdbMovieId(addToWatchList.getTmdbMovieId());


            String title = addToWatchList.getTitle();
            if (title != null && title.length() > 500) {
                title = title.substring(0, 497) + "...";
            }
            watchlistItem.setTitle(title);

            watchlistItem.setOverview(addToWatchList.getOverview());

            String posterPath = addToWatchList.getPosterPath();
            if (posterPath != null && posterPath.length() > 1000) {
                posterPath = posterPath.substring(0, 1000);
            }
            watchlistItem.setPosterPath(posterPath);

            watchlistItem.setReleaseDate(addToWatchList.getReleaseDate());

            watchlistItemRepo.save(watchlistItem);
            return ResponseEntity.ok().body(watchlistItem);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding to watchlist: " + e.getMessage());
        }
    }

    @DeleteMapping("/{tmdbId}")
    @Transactional
    public ResponseEntity<?> removeMovieFromWatchList(@PathVariable Long tmdbId){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();
            User user = userService.findUserByEmail(email);
            Optional<WatchlistItem> watchlistItemOptional = watchlistItemRepo.findByUserAndTmdbMovieId(user, tmdbId);

            if(watchlistItemOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No such movie exists in watchlist");
            }

            watchlistItemRepo.delete(watchlistItemOptional.get());
            return ResponseEntity.ok().body("Movie removed from watchlist");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing from watchlist: " + e.getMessage());
        }
    }
}
