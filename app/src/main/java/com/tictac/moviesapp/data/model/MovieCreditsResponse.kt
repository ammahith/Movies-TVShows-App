package com.tictac.moviesapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MovieCreditsResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<CastMember>,

    @SerializedName("crew")
    val crew: List<CrewMember>
)

@Parcelize
data class CastMember(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("order")
    val order: Int
) : Parcelable

@Parcelize
data class CrewMember(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("job")
    val job: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("profile_path")
    val profilePath: String?
) : Parcelable