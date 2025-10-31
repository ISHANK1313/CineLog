import React, { useState, useEffect } from 'react';
import { Search as SearchIcon, X, Film } from 'lucide-react';
import { movieAPI, watchlistAPI } from '../services/api';
import MovieCard from '../src/components/MovieCard';
import MovieDetailsModal from '../src/components/MovieDetailsModal';
import Loading from '../src/components/Loading';
import ErrorMessage from '../src/components/ErrorMessage';
import { useAuth } from '../context/AuthContext'; // Import useAuth

const Search = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [watchlistIds, setWatchlistIds] = useState(new Set());
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  // Use auth context to check for login status
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    // Only fetch watchlist if user is authenticated
    if (isAuthenticated) {
      refreshWatchlist();
    }
  }, [isAuthenticated]);

  const refreshWatchlist = async () => {
    const result = await watchlistAPI.getWatchlist();
    if (result.success) {
      const ids = new Set(result.data.map((item) => item.tmdbMovieId));
      setWatchlistIds(ids);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query) {
      setError('Please enter something to search');
      return;
    }

    setLoading(true);
    setError('');
    setHasSearched(true);

    try {
      const result = await movieAPI.searchMovies(query);
      if (result.success) {
        setResults(result.data.results || []);
      } else {
        setError(result.message || 'Failed to fetch search results');
        setResults([]);
      }
    } catch (err) {
      setError('An error occurred while searching');
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-dark-900">
      <div className="container mx-auto px-4 py-12">
        {/* Search Bar */}
        <form onSubmit={handleSearch} className="max-w-2xl mx-auto mb-12">
          <div className="relative">
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search for a movie..."
              className="w-full bg-dark-700 text-white border border-dark-600 rounded-lg px-4 py-4 pl-12 focus:outline-none focus:ring-2 focus:ring-primary-500 transition-all duration-300 placeholder-gray-500"
            />
            <SearchIcon className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            {query && (
              <button
                type="button"
                onClick={() => setQuery('')}
                className="absolute right-14 top-1/2 -translate-y-1/2 p-1 text-gray-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            )}
            <button
              type="submit"
              className="absolute right-2 top-1/2 -translate-y-1/2 btn-primary py-2 px-4"
              disabled={loading}
            >
              {loading ? '...' : 'Search'}
            </button>
          </div>
        </form>

        <ErrorMessage message={error} onClose={() => setError('')} />

        {/* Results */}
        {loading ? (
          <Loading />
        ) : (
          <div>
            {!hasSearched && (
              <div className="text-center py-20 text-gray-500">
                <SearchIcon className="w-24 h-24 mx-auto mb-4" />
                <h2 className="text-2xl font-semibold">Search CineLog</h2>
                <p>Find your next favorite movie.</p>
              </div>
            )}
            {hasSearched && results.length === 0 && (
              <div className="text-center py-20 text-gray-500">
                <Film className="w-24 h-24 mx-auto mb-4" />
                <h2 className="text-2xl font-semibold">No results found</h2>
                <p>Try a different search query.</p>
              </div>
            )}
            {results.length > 0 && (
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
                {results.map((movie) => (
                  <MovieCard
                    key={movie.id}
                    movie={movie}
                    isInWatchlist={watchlistIds.has(movie.id)}
                    onWatchlistChange={refreshWatchlist}
                    onDetailsClick={(movie) => setSelectedMovieId(movie.id)}
                  />
                ))}
              </div>
            )}
          </div>
        )}
      </div>

      {/* Movie Details Modal */}
      {selectedMovieId && (
        <MovieDetailsModal
          movieId={selectedMovieId}
          onClose={() => setSelectedMovieId(null)}
          isInWatchlist={watchlistIds.has(selectedMovieId)}
          onWatchlistChange={refreshWatchlist}
        />
      )}
    </div>
  );
};

export default Search;