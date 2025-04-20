package com.tictac.moviesapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.MovieCreditsResponse
import com.tictac.moviesapp.data.model.TVShowDetailResponse
import com.tictac.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

class TVShowDetailViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _tvShow = MutableLiveData<TVShowDetailResponse>()
    val tvShow: LiveData<TVShowDetailResponse> = _tvShow

    private val _tvShowCredits = MutableLiveData<MovieCreditsResponse>()
    val tvShowCredits: LiveData<MovieCreditsResponse> = _tvShowCredits

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchTVShowDetails(tvShowId: Int) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val response = repository.getTVShowDetails(tvShowId)
                if (response.isSuccessful) {
                    _tvShow.value = response.body()

                    // Fetch credits as well
                    fetchTVShowCredits(tvShowId)
                } else {
                    _errorMessage.value = "Error fetching TV show details: ${response.message()}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun fetchTVShowCredits(tvShowId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTVShowCredits(tvShowId)
                if (response.isSuccessful) {
                    _tvShowCredits.value = response.body()
                } else {
                    _errorMessage.value = "Error fetching TV show credits: ${response.message()}"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}