import React, { useState, useEffect } from 'react';
import { Bookmark, Trash2 } from 'lucide-react';
import { watchlistAPI } from '../services/api';
import MovieDetailsModal from '../src/components/MovieDetailsModal';
import Loading from '../src/components/Loading';
import ErrorMessage from '../src/components/ErrorMessage';
import SuccessMessage from '../src/components/SuccessMessage';

const Watchlist = () => {
  const [watchlist, setWatchlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [removingId, setRemovingId] = useState(null);

  useEffect(() => {
    fetchWatchlist();
  }, []);

  const fetchWatchlist = async () => {
    setLoading(true);
    setError('');

    try {
      const result = await watchlistAPI.getWatchlist();
      if (result.success) {
        setWatchlist(result.data || []);
      } else {
        setError(result.message || 'Failed to load watchlist');
      }
    } catch (err) {
      setError('An error occurred while loading your watchlist');
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (tmdbMovieId, title) => {
    setRemovingId(tmdbMovieId);
    setError('');
    setSuccess('');

    try {
      const result = await watchlistAPI.removeFromWatchlist(tmdbMovieId);
      if (result.success) {
        setWatchlist((prev) => prev.filter((item) => item.tmdbMovieId !== tmdbMovieId));
        setSuccess(`"${title}" removed from watchlist`);
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(result.message || 'Failed to remove movie');
      }
    } catch (err) {
      setError('An error occurred while removing the movie');
    } finally {
      setRemovingId(null);
    }
  };

  if (loading) {
    return <Loading fullScreen />;
  }

  return (
    <div className="min-h-screen bg-dark-900">
      <div className="container mx-auto px-4 py-12">
        {/* Header */}
        <div className="mb-12">
          <div className="flex items-center justify-center mb-4">
            <Bookmark className="w-12 h-12 text-primary-500 mr-3" />
            <h1 className="text-4xl md:text-5xl font-bold gradient-text">My Watchlist</h1>
          </div>
          <p className="text-center text-gray-400">
            {watchlist.length} {watchlist.length === 1 ? 'movie' : 'movies'} in your watchlist
          </p>
        </div>

        <ErrorMessage message={error} onClose={() => setError('')} />
        <SuccessMessage message={success} onClose={() => setSuccess('')} />

        {/* Watchlist Grid */}
        {watchlist.length === 0 ? (
          <div className="text-center py-20">
            <Bookmark className="w-24 h-24 text-gray-700 mx-auto mb-4" />
            <h2 className="text-2xl font-semibold mb-2">Your watchlist is empty</h2>
            <p className="text-gray-400 mb-6">
              Start adding movies to keep track of what you want to watch
            </p>
            <a
              href="/search"
              className="btn-primary inline-block"
            >
              Browse Movies
            </a>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
            {watchlist.map((item) => {
              const posterUrl = item.posterPath
                ? `https://image.tmdb.org/t/p/w500${item.posterPath}`
                : 'https://via.placeholder.com/500x750/1a1a1a/ef4444?text=No+Image';

              return (
                <div
                  key={item.id}
                  className="movie-card group animate-fade-in"
                >
                  <div className="relative aspect-[2/3] overflow-hidden">
                    <img
                      src={posterUrl}
                      alt={item.title}
                      className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110 cursor-pointer"
                      onClick={() => setSelectedMovieId(item.tmdbMovieId)}
                      onError={(e) => {
                        e.target.src = 'https://via.placeholder.com/500x750/1a1a1a/ef4444?text=No+Image';
                      }}
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                      <div className="absolute bottom-0 left-0 right-0 p-4">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            handleRemove(item.tmdbMovieId, item.title);
                          }}
                          disabled={removingId === item.tmdbMovieId}
                          className={`w-full py-2 px-4 rounded-lg font-semibold transition-all duration-300 flex items-center justify-center bg-red-500 hover:bg-red-600 ${
                            removingId === item.tmdbMovieId ? 'opacity-50 cursor-not-allowed' : ''
                          }`}
                        >
                          {removingId === item.tmdbMovieId ? (
                            <>
                              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                              Removing...
                            </>
                          ) : (
                            <>
                              <Trash2 className="w-4 h-4 mr-2" />
                              Remove
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  </div>
                  <div className="p-4">
                    <h3
                      className="font-semibold text-lg mb-2 line-clamp-1 cursor-pointer group-hover:text-primary-400 transition-colors"
                      onClick={() => setSelectedMovieId(item.tmdbMovieId)}
                    >
                      {item.title}
                    </h3>
                    {item.releaseDate && (
                      <p className="text-gray-400 text-sm mb-2">
                        {new Date(item.releaseDate).getFullYear()}
                      </p>
                    )}
                    {item.overview && (
                      <p className="text-gray-400 text-sm line-clamp-2">{item.overview}</p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Movie Details Modal */}
      {selectedMovieId && (
        <MovieDetailsModal
          movieId={selectedMovieId}
          onClose={() => setSelectedMovieId(null)}
          isInWatchlist={true}
          onWatchlistChange={fetchWatchlist}
        />
      )}
    </div>
  );
};

export default Watchlist;