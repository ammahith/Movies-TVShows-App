package com.tictac.moviesapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictac.moviesapp.data.model.Person
import com.tictac.moviesapp.data.repository.PersonRepository
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val repository = PersonRepository()

    private val _people = MutableLiveData<List<Person>>()
    val people: LiveData<List<Person>> = _people

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isSearchMode = MutableLiveData<Boolean>()
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    // Current page for pagination
    private var currentPage = 1

    fun fetchPopularPeople() {
        _isLoading.value = true
        _errorMessage.value = ""
        _isSearchMode.value = false

        // Reset pagination
        currentPage = 1

        viewModelScope.launch {
            try {
                val response = repository.getPopularPeople()
                if (response.isSuccessful) {
                    val peopleResponse = response.body()
                    _people.value = peopleResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading popular people: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMorePeople() {
        if (_isLoading.value == true) return

        _isLoading.value = true
        currentPage++

        viewModelScope.launch {
            try {
                val response = repository.getPopularPeople(page = currentPage)
                if (response.isSuccessful) {
                    val newPeople = response.body()?.results ?: emptyList()
                    val currentPeople = _people.value ?: emptyList()
                    _people.value = currentPeople + newPeople
                } else {
                    _errorMessage.value = "Error loading more people: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchPeople(query: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        _isSearchMode.value = true

        // Reset pagination for search
        currentPage = 1

        viewModelScope.launch {
            try {
                val response = repository.searchPeople(query)
                if (response.isSuccessful) {
                    val peopleResponse = response.body()
                    _people.value = peopleResponse?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error searching people: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}