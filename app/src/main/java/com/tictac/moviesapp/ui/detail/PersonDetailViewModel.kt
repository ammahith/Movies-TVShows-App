package com.tictac.moviesapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.PersonCombinedCreditsResponse
import com.tictac.moviesapp.data.model.PersonDetailResponse
import com.tictac.moviesapp.data.repository.PersonRepository
import kotlinx.coroutines.launch

class PersonDetailViewModel : ViewModel() {
    private val repository = PersonRepository()

    private val _person = MutableLiveData<PersonDetailResponse>()
    val person: LiveData<PersonDetailResponse> = _person

    private val _personCredits = MutableLiveData<PersonCombinedCreditsResponse>()
    val personCredits: LiveData<PersonCombinedCreditsResponse> = _personCredits

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchPersonDetails(personId: Int) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val response = repository.getPersonDetails(personId)
                if (response.isSuccessful) {
                    _person.value = response.body()
                } else {
                    _errorMessage.value = "Error fetching person details: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPersonCredits(personId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPersonCombinedCredits(personId)
                if (response.isSuccessful) {
                    _personCredits.value = response.body()
                } else {
                    _errorMessage.value = "Error fetching person credits: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }
}