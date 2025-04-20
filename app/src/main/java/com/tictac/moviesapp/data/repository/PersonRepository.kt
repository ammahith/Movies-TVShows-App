package com.tictac.moviesapp.data.repository

import com.tictac.moviesapp.data.api.TMDBApiClient
import com.tictac.moviesapp.data.model.PersonCombinedCreditsResponse
import com.tictac.moviesapp.data.model.PersonDetailResponse
import com.tictac.moviesapp.data.model.PersonResponse
import com.tictac.moviesapp.utils.Constants
import retrofit2.Response

class PersonRepository {
    private val apiService = TMDBApiClient.apiService

    suspend fun getPopularPeople(page: Int = 1): Response<PersonResponse> {
        return apiService.getPopularPeople(Constants.API_KEY, page)
    }

    suspend fun getPersonDetails(personId: Int): Response<PersonDetailResponse> {
        return apiService.getPersonDetails(personId, Constants.API_KEY)
    }

    suspend fun getPersonCombinedCredits(personId: Int): Response<PersonCombinedCreditsResponse> {
        return apiService.getPersonCombinedCredits(personId, Constants.API_KEY)
    }

    suspend fun searchPeople(query: String, page: Int = 1): Response<PersonResponse> {
        return apiService.searchPeople(Constants.API_KEY, query, page)
    }
}