package com.tictac.moviesapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.MovieCreditsResponse
import com.tictac.moviesapp.data.model.MovieDetailResponse
import com.tictac.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {

    private val repository = MovieRepository()

    // LiveData for movie details
    private val _movieDetails = MutableLiveData<MovieDetailResponse>()
    val movieDetails: LiveData<MovieDetailResponse> = _movieDetails

    // LiveData for movie credits
    private val _movieCredits = MutableLiveData<MovieCreditsResponse>()
    val movieCredits: LiveData<MovieCreditsResponse> = _movieCredits

    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getMovieDetails(movieId: Int) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val response = repository.getMovieDetails(movieId)
                if (response.isSuccessful) {
                    response.body()?.let { movieDetail ->
                        _movieDetails.value = movieDetail

                        // After loading details, fetch credits
                        fetchMovieCredits(movieId)
                    }
                } else {
                    _errorMessage.value = "Error loading movie details: ${response.message()}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun fetchMovieCredits(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCredits(movieId)
                if (response.isSuccessful) {
                    _movieCredits.value = response.body()
                } else {
                    _errorMessage.value = "Error loading movie credits: ${response.message()}"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}