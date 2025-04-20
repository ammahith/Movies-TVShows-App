package com.tictac.moviesapp.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tictac.moviesapp.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// View Extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Context Extensions
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// ImageView Extensions
fun ImageView.loadPosterImage(posterPath: String?) {
    if (posterPath.isNullOrEmpty()) {
        // Use the new placeholder image
        Glide.with(context)
            .load(R.drawable.image_placeholder)
            .into(this)
        return
    }

    val imageUrl = Constants.POSTER_BASE_URL + posterPath
    Glide.with(context)
        .load(imageUrl)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
        )
        .into(this)
}

fun ImageView.loadBackdropImage(backdropPath: String?) {
    if (backdropPath.isNullOrEmpty()) {
        // Use the new placeholder image
        Glide.with(context)
            .load(R.drawable.image_placeholder)
            .into(this)
        return
    }

    val imageUrl = Constants.BACKDROP_BASE_URL + backdropPath
    Glide.with(context)
        .load(imageUrl)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
        )
        .into(this)
}

fun ImageView.loadProfileImage(profilePath: String?) {
    if (profilePath.isNullOrEmpty()) {
        // Use the new placeholder image
        Glide.with(context)
            .load(R.drawable.image_placeholder)
            .into(this)
        return
    }

    val imageUrl = Constants.PROFILE_BASE_URL + profilePath
    Glide.with(context)
        .load(imageUrl)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
        )
        .into(this)
}

// String Extensions
fun String.formatDate(): String {
    if (this.isEmpty()) return ""

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: ParseException) {
        this
    }
}

// Double Extensions
fun Double.formatRating(): String {
    return String.format("%.1f", this)
}