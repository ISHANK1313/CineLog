package com.cinelog.CineLog.repository;

import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WatchlistItemRepo extends JpaRepository<WatchlistItem,Long> {
    public Optional<WatchlistItem> findById(Long id);
    public Optional<List<WatchlistItem>> findByUser(User user);
    public boolean existsById(Long id);
    public boolean existsByUser(User user);

}
