import React, { useState, useEffect } from 'react';
import { TrendingUp, Flame } from 'lucide-react';
import { movieAPI, watchlistAPI } from '../services/api';
import MovieCard from '../components/MovieCard';
import MovieDetailsModal from '../components/MovieDetailsModal';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';

const Home = () => {
  const [trendingMovies, setTrendingMovies] = useState([]);
  const [popularMovies, setPopularMovies] = useState([]);
  const [watchlistIds, setWatchlistIds] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [popularPage, setPopularPage] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    setError('');

    try {
      const [trendingResult, popularResult, watchlistResult] = await Promise.all([
        movieAPI.getTrendingMovies(),
        movieAPI.getPopularMovies(1),
        watchlistAPI.getWatchlist(),
      ]);

      if (trendingResult.success) {
        setTrendingMovies(trendingResult.data.results || []);
      }

      if (popularResult.success) {
        setPopularMovies(popularResult.data.results || []);
      }

      if (watchlistResult.success) {
        const ids = new Set(watchlistResult.data.map((item) => item.tmdbMovieId));
        setWatchlistIds(ids);
      }
    } catch (err) {
      setError('Failed to load movies. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const loadMorePopular = async () => {
    setLoadingMore(true);
    const nextPage = popularPage + 1;

    try {
      const result = await movieAPI.getPopularMovies(nextPage);
      if (result.success && result.data.results) {
        setPopularMovies((prev) => [...prev, ...result.data.results]);
        setPopularPage(nextPage);
      }
    } catch (err) {
      console.error('Failed to load more movies');
    } finally {
      setLoadingMore(false);
    }
  };

  const refreshWatchlist = async () => {
    const result = await watchlistAPI.getWatchlist();
    if (result.success) {
      const ids = new Set(result.data.map((item) => item.tmdbMovieId));
      setWatchlistIds(ids);
    }
  };

  if (loading) {
    return <Loading fullScreen />;
  }

  return (
    <div className="min-h-screen bg-dark-900">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-br from-primary-900 via-dark-800 to-dark-900 py-16 px-4">
        <div className="container mx-auto text-center">
          <h1 className="text-5xl md:text-6xl font-bold mb-4 animate-fade-in text-shadow">
            Welcome to <span className="gradient-text">CineLog</span>
          </h1>
          <p className="text-xl text-gray-300 mb-8 animate-slide-up">
            Discover and track your favorite movies
          </p>
        </div>
      </div>

      <div className="container mx-auto px-4 py-12">
        <ErrorMessage message={error} onClose={() => setError('')} />

        {/* Trending Movies */}
        <section className="mb-16">
          <div className="flex items-center mb-8">
            <Flame className="w-8 h-8 text-primary-500 mr-3" />
            <h2 className="section-title mb-0">Trending Today</h2>
          </div>

          {trendingMovies.length === 0 ? (
            <p className="text-gray-400 text-center py-12">No trending movies available</p>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
              {trendingMovies.map((movie) => (
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
        </section>

        {/* Popular Movies */}
        <section>
          <div className="flex items-center mb-8">
            <TrendingUp className="w-8 h-8 text-primary-500 mr-3" />
            <h2 className="section-title mb-0">Popular Movies</h2>
          </div>

          {popularMovies.length === 0 ? (
            <p className="text-gray-400 text-center py-12">No popular movies available</p>
          ) : (
            <>
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
                {popularMovies.map((movie) => (
                  <MovieCard
                    key={movie.id}
                    movie={movie}
                    isInWatchlist={watchlistIds.has(movie.id)}
                    onWatchlistChange={refreshWatchlist}
                    onDetailsClick={(movie) => setSelectedMovieId(movie.id)}
                  />
                ))}
              </div>

              {/* Load More Button */}
              <div className="flex justify-center mt-8">
                <button
                  onClick={loadMorePopular}
                  disabled={loadingMore}
                  className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loadingMore ? (
                    <div className="flex items-center">
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                      Loading...
                    </div>
                  ) : (
                    'Load More'
                  )}
                </button>
              </div>
            </>
          )}
        </section>
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

export default Home;