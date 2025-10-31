import React, { useEffect, useState } from 'react';
import { X, Star, Calendar, Clock, DollarSign, Plus, Check } from 'lucide-react';
import { movieAPI, watchlistAPI } from '../../services/api';
import Loading from './Loading';

const MovieDetailsModal = ({ movieId, onClose, isInWatchlist: initialWatchlistState, onWatchlistChange }) => {
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isInWatchlist, setIsInWatchlist] = useState(initialWatchlistState);
  const [addingToWatchlist, setAddingToWatchlist] = useState(false);

  useEffect(() => {
    const fetchMovieDetails = async () => {
      setLoading(true);
      const result = await movieAPI.getMovieDetails(movieId);
      if (result.success) {
        setMovie(result.data);
      }
      setLoading(false);
    };

    if (movieId) {
      fetchMovieDetails();
    }
  }, [movieId]);

  const handleWatchlistToggle = async () => {
    setAddingToWatchlist(true);
    try {
      if (isInWatchlist) {
        const result = await watchlistAPI.removeFromWatchlist(movieId);
        if (result.success) {
          setIsInWatchlist(false);
          if (onWatchlistChange) onWatchlistChange();
        }
      } else {
        const result = await watchlistAPI.addToWatchlist({
          id: movie.id,
          title: movie.title,
          overview: movie.overview,
          poster_path: movie.poster_path,
          release_date: movie.release_date,
        });
        if (result.success) {
          setIsInWatchlist(true);
          if (onWatchlistChange) onWatchlistChange();
        }
      }
    } catch (error) {
      console.error('Watchlist error:', error);
    } finally {
      setAddingToWatchlist(false);
    }
  };

  if (loading || !movie) {
    return (
      <div className="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
        <div className="bg-dark-800 rounded-xl p-8 max-w-4xl w-full">
          <Loading />
        </div>
      </div>
    );
  }

  const posterUrl = movie.poster_path
    ? `https://image.tmdb.org/t/p/w500${movie.poster_path}`
    : 'https://via.placeholder.com/500x750/1a1a1a/ef4444?text=No+Image';

  const backdropUrl = movie.backdrop_path
    ? `https://image.tmdb.org/t/p/original${movie.backdrop_path}`
    : null;

  return (
    <div className="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-dark-800 rounded-xl max-w-4xl w-full my-8 animate-scale-in">
        {/* Backdrop */}
        {backdropUrl && (
          <div className="relative h-64 rounded-t-xl overflow-hidden">
            <img
              src={backdropUrl}
              alt={movie.title}
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-dark-800 to-transparent" />
          </div>
        )}

        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 bg-black/50 rounded-full hover:bg-black/70 transition-colors"
        >
          <X className="w-6 h-6" />
        </button>

        <div className="p-6">
          <div className="flex flex-col md:flex-row gap-6">
            {/* Poster */}
            <div className="flex-shrink-0">
              <img
                src={posterUrl}
                alt={movie.title}
                className="w-48 rounded-lg shadow-2xl"
                onError={(e) => {
                  e.target.src = 'https://via.placeholder.com/500x750/1a1a1a/ef4444?text=No+Image';
                }}
              />
            </div>

            {/* Details */}
            <div className="flex-1">
              <h2 className="text-3xl font-bold mb-2">{movie.title}</h2>
              {movie.tagline && (
                <p className="text-gray-400 italic mb-4">{movie.tagline}</p>
              )}

              <div className="flex flex-wrap items-center gap-4 mb-4">
                {movie.vote_average > 0 && (
                  <div className="flex items-center bg-yellow-500/20 px-3 py-1 rounded-lg">
                    <Star className="w-5 h-5 text-yellow-400 mr-1" fill="currentColor" />
                    <span className="font-semibold">{movie.vote_average.toFixed(1)}</span>
                  </div>
                )}
                {movie.release_date && (
                  <div className="flex items-center text-gray-400">
                    <Calendar className="w-5 h-5 mr-1" />
                    {new Date(movie.release_date).getFullYear()}
                  </div>
                )}
                {movie.runtime > 0 && (
                  <div className="flex items-center text-gray-400">
                    <Clock className="w-5 h-5 mr-1" />
                    {Math.floor(movie.runtime / 60)}h {movie.runtime % 60}m
                  </div>
                )}
              </div>

              {movie.overview && (
                <div className="mb-4">
                  <h3 className="text-xl font-semibold mb-2">Overview</h3>
                  <p className="text-gray-300">{movie.overview}</p>
                </div>
              )}

              <div className="grid grid-cols-2 gap-4 mb-4">
                {movie.budget > 0 && (
                  <div>
                    <h4 className="text-gray-400 text-sm mb-1">Budget</h4>
                    <p className="font-semibold">
                      ${(movie.budget / 1000000).toFixed(1)}M
                    </p>
                  </div>
                )}
                {movie.revenue > 0 && (
                  <div>
                    <h4 className="text-gray-400 text-sm mb-1">Revenue</h4>
                    <p className="font-semibold">
                      ${(movie.revenue / 1000000).toFixed(1)}M
                    </p>
                  </div>
                )}
                {movie.status && (
                  <div>
                    <h4 className="text-gray-400 text-sm mb-1">Status</h4>
                    <p className="font-semibold">{movie.status}</p>
                  </div>
                )}
                {movie.original_language && (
                  <div>
                    <h4 className="text-gray-400 text-sm mb-1">Language</h4>
                    <p className="font-semibold uppercase">{movie.original_language}</p>
                  </div>
                )}
              </div>

              <button
                onClick={handleWatchlistToggle}
                disabled={addingToWatchlist}
                className={`w-full py-3 px-6 rounded-lg font-semibold transition-all duration-300 flex items-center justify-center ${
                  isInWatchlist
                    ? 'bg-green-500 hover:bg-green-600'
                    : 'bg-primary-600 hover:bg-primary-700'
                } ${addingToWatchlist ? 'opacity-50 cursor-not-allowed' : ''}`}
              >
                {isInWatchlist ? (
                  <>
                    <Check className="w-5 h-5 mr-2" />
                    In Watchlist
                  </>
                ) : (
                  <>
                    <Plus className="w-5 h-5 mr-2" />
                    Add to Watchlist
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetailsModal;
