package com.tictac.moviesapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tictac.moviesapp.R
import com.tictac.moviesapp.data.model.Movie
import com.tictac.moviesapp.databinding.ItemTvShowBinding
import com.tictac.moviesapp.utils.formatDate
import com.tictac.moviesapp.utils.loadPosterImage
import java.text.DecimalFormat

class TVShowAdapter(private val onItemClick: (Movie) -> Unit) :
    ListAdapter<Movie, TVShowAdapter.TVShowViewHolder>(DIFF_CALLBACK) {

    // Format rating to show only one decimal place
    private val ratingFormat = DecimalFormat("#.#")

    // Keep track of the current list for pagination
    private val currentTVShowList = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVShowViewHolder {
        val binding = ItemTvShowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TVShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TVShowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Movie>?) {
        if (list != null && list != currentTVShowList) {
            currentTVShowList.clear()
            currentTVShowList.addAll(list)
        }
        super.submitList(list?.toList()) // Create a new list to force DiffUtil to run
    }

    inner class TVShowViewHolder(private val binding: ItemTvShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tvShow: Movie) {
            // TV shows use "name" instead of "title" in API responses
            binding.tvShowTitle.text = tvShow.name ?: tvShow.title ?: "Unknown Title"

            // Format date (TV shows use firstAirDate instead of releaseDate)
            if (!tvShow.firstAirDate.isNullOrEmpty()) {
                binding.tvShowDate.text = tvShow.firstAirDate.formatDate()
            } else if (!tvShow.releaseDate.isNullOrEmpty()) {
                binding.tvShowDate.text = tvShow.releaseDate.formatDate()
            } else {
                binding.tvShowDate.text = "Unknown Date"
            }

            // Format rating to one decimal place
            binding.tvShowRating.text = ratingFormat.format(tvShow.rating)

            // Load image with null safety
            binding.ivShowPoster.loadPosterImage(tvShow.posterPath)

            // Set color of rating badge based on rating
            val ratingBackground = when {
                tvShow.rating >= 7.0 -> R.drawable.rating_background_good
                tvShow.rating >= 5.0 -> R.drawable.rating_background_average
                else -> R.drawable.rating_background_bad
            }
            binding.tvShowRating.background = ContextCompat.getDrawable(
                binding.root.context,
                ratingBackground
            )

            // Set click listener to navigate to details
            binding.root.setOnClickListener {
                onItemClick(tvShow)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}