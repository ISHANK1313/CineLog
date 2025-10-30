import React, { useState } from 'react';
import { Star, Plus, Check, Calendar, Info } from 'lucide-react';
import { watchlistAPI } from '../services/api';

const MovieCard = ({ movie, isInWatchlist = false, onWatchlistChange, onDetailsClick }) => {
  const [addingToWatchlist, setAddingToWatchlist] = useState(false);
  const [inWatchlist, setInWatchlist] = useState(isInWatchlist);

  const posterUrl = movie.poster_path || movie.posterPath
    ? `https://image.tmdb.org/t/p/w500${movie.poster_path || movie.posterPath}`
    : '/placeholder-movie.jpg';

  const handleWatchlistToggle = async (e) => {
    e.stopPropagation();
    setAddingToWatchlist(true);

    try {
      if (inWatchlist) {
        const result = await watchlistAPI.removeFromWatchlist(movie.id || movie.tmdbMovieId);
        if (result.success) {
          setInWatchlist(false);
          if (onWatchlistChange) onWatchlistChange();
        }
      } else {
        const result = await watchlistAPI.addToWatchlist(movie);
        if (result.success) {
          setInWatchlist(true);
          if (onWatchlistChange) onWatchlistChange();
        }
      }
    } catch (error) {
      console.error('Watchlist error:', error);
    } finally {
      setAddingToWatchlist(false);
    }
  };

  const handleCardClick = () => {
    if (onDetailsClick) {
      onDetailsClick(movie);
    }
  };

  return (
    <div className="movie-card group" onClick={handleCardClick}>
      <div className="relative aspect-[2/3] overflow-hidden">
        <img
          src={posterUrl}
          alt={movie.title}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
          onError={(e) => {
            e.target.src = 'https://via.placeholder.com/500x750/1a1a1a/ef4444?text=No+Image';
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <div className="absolute bottom-0 left-0 right-0 p-4">
            <button
              onClick={handleWatchlistToggle}
              disabled={addingToWatchlist}
              className={`w-full py-2 px-4 rounded-lg font-semibold transition-all duration-300 flex items-center justify-center ${
                inWatchlist
                  ? 'bg-green-500 hover:bg-green-600'
                  : 'bg-primary-600 hover:bg-primary-700'
              } ${addingToWatchlist ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              {inWatchlist ? (
                <>
                  <Check className="w-4 h-4 mr-2" />
                  In Watchlist
                </>
              ) : (
                <>
                  <Plus className="w-4 h-4 mr-2" />
                  Add to Watchlist
                </>
              )}
            </button>
          </div>
        </div>
        {(movie.vote_average || movie.voteAverage) && (
          <div className="absolute top-2 right-2 bg-black/70 backdrop-blur-sm px-2 py-1 rounded-lg flex items-center">
            <Star className="w-4 h-4 text-yellow-400 mr-1" fill="currentColor" />
            <span className="text-sm font-semibold">
              {(movie.vote_average || movie.voteAverage)?.toFixed(1) || 'N/A'}
            </span>
          </div>
        )}
      </div>
      <div className="p-4">
        <h3 className="font-semibold text-lg mb-2 line-clamp-1 group-hover:text-primary-400 transition-colors">
          {movie.title}
        </h3>
        {(movie.release_date || movie.releaseDate) && (
          <div className="flex items-center text-gray-400 text-sm mb-2">
            <Calendar className="w-4 h-4 mr-1" />
            {new Date(movie.release_date || movie.releaseDate).getFullYear()}
          </div>
        )}
        {movie.genres && movie.genres.length > 0 && (
          <div className="flex flex-wrap gap-1 mb-2">
            {movie.genres.slice(0, 3).map((genre, index) => (
              <span
                key={index}
                className="text-xs bg-dark-700 px-2 py-1 rounded-full text-gray-300"
              >
                {genre}
              </span>
            ))}
          </div>
        )}
        {movie.overview && (
          <p className="text-gray-400 text-sm line-clamp-2">{movie.overview}</p>
        )}
      </div>
    </div>
  );
};

export default MovieCard;