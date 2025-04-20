package com.tictac.moviesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVShowDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val rating: Double,

    @SerializedName("first_air_date")
    val releaseDate: String,

    @SerializedName("genres")
    val genres: List<Genre>,

    @SerializedName("tagline")
    val tagline: String?,

    // TV-specific fields
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int?,

    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("networks")
    val networks: List<Network>?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("last_air_date")
    val lastAirDate: String?
) : Parcelable

@Parcelize
data class Network(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo_path")
    val logoPath: String?
) : Parcelable