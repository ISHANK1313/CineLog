import React, { useState, useEffect } from 'react';
import { Search as SearchIcon, X } from 'lucide-react';
import { movieAPI, watchlistAPI } from '../services/api';
import MovieCard from '../components/MovieCard';
import MovieDetailsModal from '../components/MovieDetailsModal';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';

const Search = () => {
  const [query, setQuery] = useState('');
  const [movies, setMovies] = useState([]);
  const [watchlistIds, setWatchlistIds] = useState(new Set());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    fetchWatchlist();
  }, []);

  const fetchWatchlist = async () => {
    const result = await watchlistAPI.getWatchlist();
    if (result.success) {
      const ids = new Set(result.data.map((item) => item.tmdbMovieId));
      setWatchlistIds(ids);
    }
  };

  const handleSearch = async (e, page = 1) => {
    if (e) e.preventDefault();

    if (!query.trim()) {
      setError('Please enter a search term');
      return;
    }

    setLoading(true);
    setError('');
    setHasSearched(true);

    try {
      const result = await movieAPI.searchMovies(query, page);
      if (result.success) {
        setMovies(result.data.results || []);
        setTotalPages(result.data.total_pages || 0);
        setCurrentPage(page);
      } else {
        setError(result.message || 'Failed to search movies');
        setMovies([]);
      }
    } catch (err) {
      setError('An error occurred while searching');
      setMovies([]);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    handleSearch(null, newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const clearSearch = () => {
    setQuery('');
    setMovies([]);
    setError('');
    setHasSearched(false);
    setCurrentPage(1);
    setTotalPages(0);
  };

  const refreshWatchlist = async () => {
    await fetchWatchlist();
  };

  return (
    <div className="min-h-screen bg-dark-900">
      <div className="container mx-auto px-4 py-12">
        {/* Search Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-bold mb-4 gradient-text">
            Search Movies
          </h1>
          <p className="text-gray-400">Find your favorite movies from our extensive database</p>
        </div>

        {/* Search Form */}
        <form onSubmit={handleSearch} className="max-w-2xl mx-auto mb-12">
          <div className="relative">
            <SearchIcon className="absolute left-4 top-1/2 transform -translate-y-1/2 w-6 h-6 text-gray-400" />
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search for movies..."
              className="input-field pl-14 pr-14 text-lg"
            />
            {query && (
              <button
                type="button"
                onClick={clearSearch}
                className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            )}
          </div>
          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full mt-4 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? (
              <div className="flex items-center justify-center">
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                Searching...
              </div>
            ) : (
              'Search'
            )}
          </button>
        </form>

        <ErrorMessage message={error} onClose={() => setError('')} />

        {/* Search Results */}
        {loading ? (
          <Loading />
        ) : hasSearched ? (
          movies.length === 0 ? (
            <div className="text-center py-12">
              <SearchIcon className="w-16 h-16 text-gray-600 mx-auto mb-4" />
              <p className="text-xl text-gray-400">No movies found for "{query}"</p>
              <p className="text-gray-500 mt-2">Try different keywords</p>
            </div>
          ) : (
            <>
              <div className="mb-6">
                <p className="text-gray-400">
                  Found {movies.length} results for "<span className="text-white font-semibold">{query}</span>"
                </p>
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
                {movies.map((movie) => (
                  <MovieCard
                    key={movie.id}
                    movie={movie}
                    isInWatchlist={watchlistIds.has(movie.id)}
                    onWatchlistChange={refreshWatchlist}
                    onDetailsClick={(movie) => setSelectedMovieId(movie.id)}
                  />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex justify-center items-center mt-12 space-x-2">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>

                  <div className="flex items-center space-x-2">
                    {[...Array(Math.min(5, totalPages))].map((_, i) => {
                      let pageNum;
                      if (totalPages <= 5) {
                        pageNum = i + 1;
                      } else if (currentPage <= 3) {
                        pageNum = i + 1;
                      } else if (currentPage >= totalPages - 2) {
                        pageNum = totalPages - 4 + i;
                      } else {
                        pageNum = currentPage - 2 + i;
                      }

                      return (
                        <button
                          key={i}
                          onClick={() => handlePageChange(pageNum)}
                          className={`w-10 h-10 rounded-lg font-semibold transition-all duration-300 ${
                            currentPage === pageNum
                              ? 'bg-primary-600 text-white'
                              : 'bg-dark-700 text-gray-300 hover:bg-dark-600'
                          }`}
                        >
                          {pageNum}
                        </button>
                      );
                    })}
                  </div>

                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )
        ) : (
          <div className="text-center py-12">
            <SearchIcon className="w-24 h-24 text-gray-700 mx-auto mb-4" />
            <p className="text-xl text-gray-400">Start searching for your favorite movies</p>
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