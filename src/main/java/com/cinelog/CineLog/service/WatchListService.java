package com.cinelog.CineLog.service;

import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.entity.WatchlistItem;
import com.cinelog.CineLog.repository.WatchlistItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WatchListService {
    @Autowired
    private WatchlistItemRepo watchlistItemRepo;

    public List<WatchlistItem> findByUser(User user){
        Optional<List<WatchlistItem>> watchlistItemOptional=watchlistItemRepo.findByUser(user);
        if(watchlistItemOptional.isEmpty()){
            return null;
        }
        return watchlistItemOptional.get();
    }

    public boolean isWatchListExistsByUser(User user){
        return watchlistItemRepo.existsByUser(user);
    }

}
