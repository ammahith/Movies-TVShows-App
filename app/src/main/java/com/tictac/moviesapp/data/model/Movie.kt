// In Movie.kt
package com.tictac.moviesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String? = null,    // For movies

    @SerializedName("name")
    val name: String? = null,     // For TV shows

    @SerializedName("overview")
    val overview: String = "",

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val rating: Double,

    @SerializedName("release_date")
    val releaseDate: String? = null,    // For movies

    @SerializedName("first_air_date")
    val firstAirDate: String? = null,   // For TV shows

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("genre_ids")
    val genreIds: List<Int> = listOf()
) : Parcelable

data class MovieResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Movie>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)

@Parcelize
data class MovieDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String? = null,    // For movies

    @SerializedName("name")
    val name: String? = null,     // For TV shows

    @SerializedName("overview")
    val overview: String = "",

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val rating: Double,

    @SerializedName("release_date")
    val releaseDate: String? = null,    // For movies

    @SerializedName("first_air_date")
    val firstAirDate: String? = null,   // For TV shows

    @SerializedName("runtime")
    val runtime: Int?,

    @SerializedName("genres")
    val genres: List<Genre> = listOf(),

    @SerializedName("budget")
    val budget: Long = 0,

    @SerializedName("revenue")
    val revenue: Long = 0,

    @SerializedName("tagline")
    val tagline: String? = null
) : Parcelable

@Parcelize
data class Genre(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
) : Parcelable