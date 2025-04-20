package com.tictac.moviesapp.data.repository

import com.tictac.moviesapp.data.api.TMDBApiClient
import com.tictac.moviesapp.data.model.*
import com.tictac.moviesapp.utils.Constants
import retrofit2.Response

class MovieRepository {
    private val apiService = TMDBApiClient.apiService

    // Movie Methods
    suspend fun getPopularMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getPopularMovies(Constants.API_KEY, page, region)
    }

    suspend fun getNowPlayingMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getNowPlayingMovies(Constants.API_KEY, page, region)
    }

    suspend fun getTopRatedMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getTopRatedMovies(Constants.API_KEY, page, region)
    }

    suspend fun getUpcomingMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getUpcomingMovies(Constants.API_KEY, page, region)
    }

    suspend fun getTrendingMovies(page: Int = 1): Response<MovieResponse> {
        return apiService.getTrendingMovies(Constants.API_KEY, page)
    }

    // In MovieRepository.kt
    suspend fun getMorePopularMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getPopularMovies(Constants.API_KEY, page, region)
    }

    suspend fun getMoreNowPlayingMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getNowPlayingMovies(Constants.API_KEY, page, region)
    }

    suspend fun getMoreTopRatedMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getTopRatedMovies(Constants.API_KEY, page, region)
    }

    suspend fun getMoreUpcomingMovies(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getUpcomingMovies(Constants.API_KEY, page, region)
    }

    suspend fun getMoreTrendingMovies(page: Int = 1): Response<MovieResponse> {
        return apiService.getTrendingMovies(Constants.API_KEY, page)
    }

    suspend fun getMovieDetails(movieId: Int): Response<MovieDetailResponse> {
        return apiService.getMovieDetails(movieId, Constants.API_KEY)
    }

    suspend fun getMovieCredits(movieId: Int): Response<MovieCreditsResponse> {
        return apiService.getMovieCredits(movieId, Constants.API_KEY)
    }

    // TV Show Methods
    suspend fun getPopularTVShows(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getPopularTVShows(Constants.API_KEY, page, region)
    }

    suspend fun getAiringTodayTVShows(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getAiringTodayTVShows(Constants.API_KEY, page, region)
    }

    suspend fun getOnTheAirTVShows(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getOnTheAirTVShows(Constants.API_KEY, page, region)
    }

    suspend fun getTopRatedTVShows(page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.getTopRatedTVShows(Constants.API_KEY, page, region)
    }

    suspend fun getTVShowDetails(tvId: Int): Response<TVShowDetailResponse> {
        return apiService.getTVShowDetails(tvId, Constants.API_KEY)
    }

    suspend fun getTVShowCredits(tvId: Int): Response<MovieCreditsResponse> {
        return apiService.getTVShowCredits(tvId, Constants.API_KEY)
    }

    // Search Methods
    suspend fun searchMovies(query: String, page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.searchMovies(Constants.API_KEY, query, page, region)
    }

    suspend fun searchTVShows(query: String, page: Int = 1, region: String = "US"): Response<MovieResponse> {
        return apiService.searchTVShows(Constants.API_KEY, query, page, region)
    }

    suspend fun searchPeople(query: String, page: Int = 1): Response<PersonResponse> {
        return apiService.searchPeople(Constants.API_KEY, query, page)
    }
}