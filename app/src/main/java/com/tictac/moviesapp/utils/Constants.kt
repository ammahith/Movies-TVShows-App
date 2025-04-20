package com.tictac.moviesapp.utils

object Constants {
    // API Related
    const val API_KEY = "ef1bbcfd14007fea7f19b1d9c8f4be49"
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
    const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280"
    const val PROFILE_BASE_URL = "https://image.tmdb.org/t/p/w300"

    // Default values
    const val STARTING_PAGE_INDEX = 1

    // Bundle Keys
    const val MOVIE_ID = "movie_id"
    const val TV_SHOW_ID = "tv_show_id"
    const val PERSON_ID = "person_id"

    // Categories
    const val POPULAR = "popular"
    const val NOW_PLAYING = "now_playing"
    const val TOP_RATED = "top_rated"
    const val UPCOMING = "upcoming"
    const val TRENDING = "trending"
    const val AIRING_TODAY = "airing_today"
    const val ON_TV = "on_the_air"

    // Error Messages
    const val NETWORK_ERROR = "Network error occurred. Please check your internet connection."
    const val GENERIC_ERROR = "Something went wrong. Please try again later."
}