package com.tictac.moviesapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.Movie
import com.tictac.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val repository = MovieRepository()

    // LiveData for movies
    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _nowPlayingMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies: LiveData<List<Movie>> = _nowPlayingMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies

    private val _trendingMovies = MutableLiveData<List<Movie>>()
    val trendingMovies: LiveData<List<Movie>> = _trendingMovies

    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Add search mode state
    private val _isSearchMode = MutableLiveData<Boolean>()
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    // Get user's region from device locale
    private val userRegion = Locale.getDefault().country

    // Current pages for pagination
    private var popularPage = 1
    private var nowPlayingPage = 1
    private var topRatedPage = 1
    private var upcomingPage = 1
    private var trendingPage = 1

    fun fetchAllMovies() {
        _isLoading.value = true
        _errorMessage.value = ""
        _isSearchMode.value = false

        // Reset pages
        popularPage = 1
        nowPlayingPage = 1
        topRatedPage = 1
        upcomingPage = 1
        trendingPage = 1

        // Fetch popular movies
        fetchPopularMovies()

        // Fetch now playing movies
        fetchNowPlayingMovies()

        // Fetch top rated movies
        fetchTopRatedMovies()

        // Fetch upcoming movies
        fetchUpcomingMovies()

        // Fetch trending movies
        fetchTrendingMovies()
    }

    private fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies(page = popularPage, region = userRegion)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _popularMovies.value = moviesResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading popular movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun fetchNowPlayingMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getNowPlayingMovies(page = nowPlayingPage, region = userRegion)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _nowPlayingMovies.value = moviesResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading now playing movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun fetchTopRatedMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getTopRatedMovies(page = topRatedPage, region = userRegion)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _topRatedMovies.value = moviesResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading top rated movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun fetchUpcomingMovies() {
        viewModelScope.launch {
            try {
                // First try with user's region
                val response = repository.getUpcomingMovies(page = upcomingPage, region = userRegion)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    val results = moviesResponse?.results ?: emptyList()

                    if (results.isNotEmpty()) {
                        _upcomingMovies.value = results
                    } else if (userRegion != "US") {
                        // If empty results and not US region, fall back to US
                        fetchUpcomingMoviesFallback()
                    } else {
                        _upcomingMovies.value = emptyList()
                    }
                } else {
                    _errorMessage.value = "Error loading upcoming movies: ${response.message()}"
                    if (userRegion != "US") {
                        fetchUpcomingMoviesFallback()
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                if (userRegion != "US") {
                    fetchUpcomingMoviesFallback()
                }
            }
        }
    }

    private fun fetchUpcomingMoviesFallback() {
        viewModelScope.launch {
            try {
                val response = repository.getUpcomingMovies(page = upcomingPage, region = "US")
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _upcomingMovies.value = moviesResponse?.results ?: emptyList()
                }
            } catch (e: Exception) {
                // Already showed error for the primary region, so just silently fail
            }
        }
    }

    private fun fetchTrendingMovies() {
        viewModelScope.launch {
            try {
                val response = repository.getTrendingMovies(page = trendingPage)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _trendingMovies.value = moviesResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading trending movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMorePopularMovies() {
        popularPage++
        viewModelScope.launch {
            try {
                val response = repository.getMorePopularMovies(page = popularPage, region = userRegion)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    val currentMovies = _popularMovies.value ?: emptyList()
                    _popularMovies.value = currentMovies + newMovies
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading more movies: ${e.message}"
            }
        }
    }

    fun loadMoreNowPlayingMovies() {
        nowPlayingPage++
        viewModelScope.launch {
            try {
                val response = repository.getMoreNowPlayingMovies(page = nowPlayingPage, region = userRegion)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    val currentMovies = _nowPlayingMovies.value ?: emptyList()
                    _nowPlayingMovies.value = currentMovies + newMovies
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading more movies: ${e.message}"
            }
        }
    }

    fun loadMoreTopRatedMovies() {
        topRatedPage++
        viewModelScope.launch {
            try {
                val response = repository.getMoreTopRatedMovies(page = topRatedPage, region = userRegion)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    val currentMovies = _topRatedMovies.value ?: emptyList()
                    _topRatedMovies.value = currentMovies + newMovies
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading more movies: ${e.message}"
            }
        }
    }

    fun loadMoreUpcomingMovies() {
        upcomingPage++
        viewModelScope.launch {
            try {
                val response = repository.getMoreUpcomingMovies(page = upcomingPage, region = userRegion)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    if (newMovies.isNotEmpty()) {
                        val currentMovies = _upcomingMovies.value ?: emptyList()
                        _upcomingMovies.value = currentMovies + newMovies
                    } else if (userRegion != "US") {
                        // Try with US region if no results
                        val usResponse = repository.getMoreUpcomingMovies(page = upcomingPage, region = "US")
                        if (usResponse.isSuccessful) {
                            val usMovies = usResponse.body()?.results ?: emptyList()
                            val currentMovies = _upcomingMovies.value ?: emptyList()
                            _upcomingMovies.value = currentMovies + usMovies
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading more movies: ${e.message}"
            }
        }
    }

    fun loadMoreTrendingMovies() {
        trendingPage++
        viewModelScope.launch {
            try {
                val response = repository.getMoreTrendingMovies(page = trendingPage)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    val currentMovies = _trendingMovies.value ?: emptyList()
                    _trendingMovies.value = currentMovies + newMovies
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading more movies: ${e.message}"
            }
        }
    }

    fun searchMovies(query: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _isSearchMode.value = true

        viewModelScope.launch {
            try {
                val response = repository.searchMovies(query, region = userRegion)
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    _popularMovies.value = moviesResponse?.results ?: emptyList()

                    // Hide other categories
                    _nowPlayingMovies.value = emptyList()
                    _topRatedMovies.value = emptyList()
                    _upcomingMovies.value = emptyList()
                    _trendingMovies.value = emptyList()
                } else {
                    _errorMessage.value = "Error searching movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}