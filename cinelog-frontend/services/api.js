import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  signup: async (email, password) => {
    try {
      const response = await api.post('/api/auth/signup', { email, password });
      return { success: true, message: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Signup failed. Please try again.',
      };
    }
  },

  login: async (email, password) => {
    try {
      const response = await api.post('/api/auth/login', { email, password });
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        return { success: true, token: response.data.token };
      }
      return { success: false, message: 'Login failed' };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Invalid email or password',
      };
    }
  },

  logout: () => {
    localStorage.removeItem('token');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
};

// Movie APIs
export const movieAPI = {
  searchMovies: async (query, page = 1, includeAdult = false) => {
    try {
      const response = await api.get('/api/movies', {
        params: {
          query,
          page,
          include_adult: includeAdult,
          language: 'en-US',
        },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to search movies',
      };
    }
  },

  getTrendingMovies: async (language = 'en-US') => {
    try {
      const response = await api.get('/api/trending', {
        params: { language },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to fetch trending movies',
      };
    }
  },

  getPopularMovies: async (page = 1, language = 'en-US') => {
    try {
      const response = await api.get('/api/popular', {
        params: { page, language },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to fetch popular movies',
      };
    }
  },

  getMovieDetails: async (movieId, language = 'en-US') => {
    try {
      const response = await api.get(`/api/movie/${movieId}`, {
        params: { language },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to fetch movie details',
      };
    }
  },
};

// Watchlist APIs
export const watchlistAPI = {
  getWatchlist: async () => {
    try {
      const response = await api.get('/watchlist');
      return { success: true, data: Array.isArray(response.data) ? response.data : [] };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to fetch watchlist',
        data: [],
      };
    }
  },

  addToWatchlist: async (movie) => {
    try {
      const payload = {
        tmdbMovieId: movie.id,
        title: movie.title,
        overview: movie.overview,
        posterPath: movie.poster_path,
        releaseDate: movie.release_date,
      };
      const response = await api.post('/watchlist', payload);
      return { success: true, data: response.data };
    } catch (error) {
      const message = error.response?.data || 'Failed to add to watchlist';
      return {
        success: false,
        message: typeof message === 'string' ? message : 'Failed to add to watchlist',
      };
    }
  },

  removeFromWatchlist: async (tmdbMovieId) => {
    try {
      const response = await api.delete(`/watchlist/${tmdbMovieId}`);
      return { success: true, message: response.data };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Failed to remove from watchlist',
      };
    }
  },
};

export default api;