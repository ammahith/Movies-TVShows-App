package com.tictac.moviesapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.Movie
import com.tictac.moviesapp.data.repository.MovieRepository
import com.tictac.moviesapp.utils.Constants
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardViewModel : ViewModel() {

    private val repository = MovieRepository()

    // LiveData for TV shows
    private val _tvShows = MutableLiveData<List<Movie>>()
    val tvShows: LiveData<List<Movie>> = _tvShows

    // Current selected category
    private val _currentCategory = MutableLiveData<String>()
    val currentCategory: LiveData<String> = _currentCategory

    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Search mode
    private val _isSearchMode = MutableLiveData<Boolean>()
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    // Current page for pagination
    private var currentPage = 1

    // User's region from device locale
    private val userRegion = Locale.getDefault().country

    init {
        _currentCategory.value = Constants.POPULAR
        _isSearchMode.value = false
    }

    fun fetchTVShowsByCategory(category: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _currentCategory.value = category
        _isSearchMode.value = false

        // Reset pagination
        currentPage = 1

        viewModelScope.launch {
            try {
                val response = when (category) {
                    Constants.POPULAR -> repository.getPopularTVShows(region = userRegion)
                    Constants.AIRING_TODAY -> repository.getAiringTodayTVShows(region = userRegion)
                    Constants.ON_TV -> repository.getOnTheAirTVShows(region = userRegion)
                    Constants.TOP_RATED -> repository.getTopRatedTVShows(region = userRegion)
                    else -> repository.getPopularTVShows(region = userRegion)
                }

                if (response.isSuccessful) {
                    val tvShowsResponse = response.body()
                    _tvShows.value = tvShowsResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading TV shows: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreTVShows() {
        if (_isLoading.value == true) return

        _isLoading.value = true
        currentPage++

        viewModelScope.launch {
            try {
                val category = _currentCategory.value ?: Constants.POPULAR
                val response = when (category) {
                    Constants.POPULAR -> repository.getPopularTVShows(page = currentPage, region = userRegion)
                    Constants.AIRING_TODAY -> repository.getAiringTodayTVShows(page = currentPage, region = userRegion)
                    Constants.ON_TV -> repository.getOnTheAirTVShows(page = currentPage, region = userRegion)
                    Constants.TOP_RATED -> repository.getTopRatedTVShows(page = currentPage, region = userRegion)
                    else -> repository.getPopularTVShows(page = currentPage, region = userRegion)
                }

                if (response.isSuccessful) {
                    val newShows = response.body()?.results ?: emptyList()
                    val currentShows = _tvShows.value ?: emptyList()
                    _tvShows.value = currentShows + newShows
                } else {
                    _errorMessage.value = "Error loading more TV shows: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchTVShows(query: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _currentCategory.value = "Search Results"
        _isSearchMode.value = true

        // Reset pagination
        currentPage = 1

        viewModelScope.launch {
            try {
                val response = repository.searchTVShows(query, region = userRegion)
                if (response.isSuccessful) {
                    val tvShowsResponse = response.body()
                    _tvShows.value = tvShowsResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error searching TV shows: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}