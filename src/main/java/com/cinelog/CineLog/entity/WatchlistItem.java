package com.cinelog.CineLog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;


    @Entity
    public class WatchlistItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        @JsonIgnore
        private User user;

        private Long tmdbMovieId;
        private String title;
        private String overview;
        private String posterPath;
        private String releaseDate;
        private LocalDateTime addedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Long getTmdbMovieId() {
            return tmdbMovieId;
        }

        public void setTmdbMovieId(Long tmdbMovieId) {
            this.tmdbMovieId = tmdbMovieId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public LocalDateTime getAddedAt() {
            return addedAt;
        }

        public void setAddedAt(LocalDateTime addedAt) {
            this.addedAt = addedAt;
        }
    }

