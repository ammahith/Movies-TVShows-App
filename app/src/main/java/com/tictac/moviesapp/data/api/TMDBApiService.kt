package com.tictac.moviesapp.data.api

import com.tictac.moviesapp.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {
    // Movie Methods
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieDetailResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieCreditsResponse>

    // TV Show Methods
    @GET("tv/popular")
    suspend fun getPopularTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("tv/airing_today")
    suspend fun getAiringTodayTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("tv/top_rated")
    suspend fun getTopRatedTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("tv/{tv_id}")
    suspend fun getTVShowDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Response<TVShowDetailResponse>

    @GET("tv/{tv_id}/credits")
    suspend fun getTVShowCredits(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieCreditsResponse>

    // Person Methods
    @GET("person/popular")
    suspend fun getPopularPeople(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<PersonResponse>

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): Response<PersonDetailResponse>

    @GET("person/{person_id}/combined_credits")
    suspend fun getPersonCombinedCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): Response<PersonCombinedCreditsResponse>

    // Search Methods
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("search/tv")
    suspend fun searchTVShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US"
    ): Response<MovieResponse>

    @GET("search/person")
    suspend fun searchPeople(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<PersonResponse>
}