package com.tictac.moviesapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tictac.moviesapp.data.model.Movie
import com.tictac.moviesapp.databinding.ItemMovieBinding
import com.tictac.moviesapp.utils.formatDate
import com.tictac.moviesapp.utils.formatRating
import com.tictac.moviesapp.utils.loadPosterImage

class MovieAdapter(private val onMovieClick: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    private val currentMovieList = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    fun addMovies(movies: List<Movie>) {
        val oldSize = currentMovieList.size
        currentMovieList.addAll(movies)
        submitList(currentMovieList.toList()) // Create a new list to trigger update
    }

    override fun submitList(list: List<Movie>?) {
        if (list != null && list != currentMovieList) {
            currentMovieList.clear()
            currentMovieList.addAll(list)
        }
        super.submitList(list?.toList()) // Create a new list to force DiffUtil to run
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClick(getItem(position))
                }
            }
        }

        fun bind(movie: Movie) {
            binding.apply {
                // Set title (use name for TV shows, title for movies)
                tvMovieTitle.text = movie.title ?: movie.name ?: "Unknown Title"

                // Set release date
                val releaseDate = if (!movie.releaseDate.isNullOrEmpty()) {
                    movie.releaseDate
                } else {
                    movie.firstAirDate
                }

                if (!releaseDate.isNullOrEmpty()) {
                    tvMovieReleaseDate.text = releaseDate.formatDate()
                } else {
                    tvMovieReleaseDate.text = "Unknown Date"
                }

                tvMovieRating.text = movie.rating.formatRating()
                ivMoviePoster.loadPosterImage(movie.posterPath)
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}