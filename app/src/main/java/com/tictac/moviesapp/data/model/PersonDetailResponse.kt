package com.tictac.moviesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("birthday")
    val birthday: String?,

    @SerializedName("deathday")
    val deathday: String?,

    @SerializedName("place_of_birth")
    val placeOfBirth: String?,

    @SerializedName("biography")
    val biography: String,

    @SerializedName("known_for_department")
    val knownForDepartment: String,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("gender")
    val gender: Int,

    @SerializedName("homepage")
    val homepage: String?,

    @SerializedName("also_known_as")
    val alsoKnownAs: List<String>?,

    @SerializedName("imdb_id")
    val imdbId: String?
) : Parcelable

@Parcelize
data class PersonCastCredit(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val movieTitle: String?,

    @SerializedName("name")
    val tvShowName: String?,

    @SerializedName("character")
    val character: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("media_type")
    val mediaType: String,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("first_air_date")
    val firstAirDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("episode_count")
    val episodeCount: Int?
) : Parcelable {
    // Handle different title field names between movies and TV shows
    val displayTitle: String
        get() = movieTitle ?: tvShowName ?: ""

    // Handle different date field names between movies and TV shows
    val displayDate: String
        get() = releaseDate ?: firstAirDate ?: ""
}

@Parcelize
data class PersonCrewCredit(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val movieTitle: String?,

    @SerializedName("name")
    val tvShowName: String?,

    @SerializedName("job")
    val job: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("media_type")
    val mediaType: String,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("first_air_date")
    val firstAirDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double
) : Parcelable {
    // Handle different title field names between movies and TV shows
    val displayTitle: String
        get() = movieTitle ?: tvShowName ?: ""

    // Handle different date field names between movies and TV shows
    val displayDate: String
        get() = releaseDate ?: firstAirDate ?: ""
}

data class PersonCombinedCreditsResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<PersonCastCredit>,

    @SerializedName("crew")
    val crew: List<PersonCrewCredit>
)