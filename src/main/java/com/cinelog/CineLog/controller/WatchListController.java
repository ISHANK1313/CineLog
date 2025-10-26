package com.cinelog.CineLog.controller;

import com.cinelog.CineLog.dto.AddToWatchList;
import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.entity.WatchlistItem;
import com.cinelog.CineLog.repository.WatchlistItemRepo;
import com.cinelog.CineLog.service.UserService;
import com.cinelog.CineLog.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

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
                return ResponseEntity.ok().body("no current watchlist...");

            }
            return ResponseEntity.ok().body(watchlistItemList);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong try again");
        }
    }
    @PostMapping
    public ResponseEntity<?> addMovieInWatchList(@RequestBody AddToWatchList addToWatchList){
     try {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String email = (String) auth.getPrincipal();
         User user = userService.findUserByEmail(email);
         WatchlistItem watchlistItem = new WatchlistItem();
         watchlistItem.setUser(user);
         watchlistItem.setAddedAt(LocalDateTime.now());
         watchlistItem.setOverview(addToWatchList.getOverview());
         watchlistItem.setTitle(addToWatchList.getTitle());
         watchlistItem.setPosterPath(addToWatchList.getPosterPath());
         watchlistItem.setReleaseDate(addToWatchList.getReleaseDate());
         watchlistItem.setTmdbMovieId(addToWatchList.getTmdbMovieId());
         watchlistItemRepo.save(watchlistItem);
         return ResponseEntity.ok().body(watchlistItem);
     }
     catch (Exception e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong try again");
     }
    }

    @DeleteMapping
    public ResponseEntity<?> removeMovieFromWatchList(@RequestParam Long tmdbId){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();
            User user= userService.findUserByEmail(email);
            Optional<WatchlistItem> watchlistItemOptional=watchlistItemRepo.findByUserAndTmdbMovieId(user,tmdbId);
            if(watchlistItemOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("no such movie exists in watchlist");

            }
            watchlistItemRepo.delete(watchlistItemOptional.get());
            return ResponseEntity.ok().body("movie removed from watchlist");

        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong try again");
        }

    }
}
