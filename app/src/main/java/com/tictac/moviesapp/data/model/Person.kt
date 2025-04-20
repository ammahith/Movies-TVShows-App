package com.tictac.moviesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("known_for_department")
    val knownForDepartment: String,

    @SerializedName("known_for")
    val knownFor: List<KnownFor>,

    @SerializedName("popularity")
    val popularity: Double
) : Parcelable

@Parcelize
data class KnownFor(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("media_type")
    val mediaType: String,

    @SerializedName("poster_path")
    val posterPath: String?
) : Parcelable

data class PersonResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Person>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)