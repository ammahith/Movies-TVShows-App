package com.tictac.moviesapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tictac.moviesapp.R
import com.tictac.moviesapp.databinding.FragmentMovieDetailBinding
import com.tictac.moviesapp.ui.adapters.CastAdapter
import com.tictac.moviesapp.utils.formatDate
import com.tictac.moviesapp.utils.loadBackdropImage
import com.tictac.moviesapp.utils.loadPosterImage
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailViewModel
    private lateinit var castAdapter: CastAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MovieDetailViewModel::class.java]

        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCastRecyclerView()

        // Get movie ID from arguments
        val movieId = arguments?.getInt("movieId") ?: -1
        if (movieId != -1) {
            viewModel.getMovieDetails(movieId)
        }

        observeViewModel()

        // Set up back button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCastRecyclerView() {
        castAdapter = CastAdapter { castMember ->
            // Navigate to person detail
            try {
                val bundle = Bundle().apply {
                    putInt("personId", castMember.id)
                }
                findNavController().navigate(R.id.personDetailFragment, bundle)
            } catch (e: Exception) {
                // Handle navigation error
                Toast.makeText(context, "Could not open person details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }
    }

    private fun observeViewModel() {
        // Format for currency
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        // Format for rating
        val ratingFormat = DecimalFormat("#.#")

        // Observe movie details
        viewModel.movieDetails.observe(viewLifecycleOwner) { movie ->
            binding.apply {
                // Handle loading state - hide content and show progressBar
                progressBar.visibility = View.VISIBLE
                contentGroup.visibility = View.GONE

                // Load images
                ivMovieBackdrop.loadBackdropImage(movie.backdropPath)
                ivMoviePoster.loadPosterImage(movie.posterPath)

                // Set text details
                tvMovieTitle.text = movie.title ?: "Unknown Title" // Handle nullable title
                tvMovieOverview.text = movie.overview ?: ""

                // Handle nullable release date
                if (!movie.releaseDate.isNullOrEmpty()) {
                    tvMovieReleaseDate.text = getString(R.string.release_date, movie.releaseDate.formatDate())
                    tvMovieReleaseDate.visibility = View.VISIBLE
                } else {
                    tvMovieReleaseDate.visibility = View.GONE
                }

                // Set rating
                if (movie.rating > 0) {
                    tvMovieRating.text = getString(R.string.rating, ratingFormat.format(movie.rating))
                    tvMovieRating.visibility = View.VISIBLE
                } else {
                    tvMovieRating.visibility = View.GONE
                }

                // Handle tagline
                if (!movie.tagline.isNullOrEmpty()) {
                    tvMovieTagline.visibility = View.VISIBLE
                    tvMovieTagline.text = movie.tagline
                } else {
                    tvMovieTagline.visibility = View.GONE
                }

                // Display runtime if available
                if (movie.runtime != null && movie.runtime > 0) {
                    val hours = movie.runtime / 60
                    val minutes = movie.runtime % 60
                    val runtimeText = if (hours > 0) {
                        "$hours h $minutes min"
                    } else {
                        "$minutes min"
                    }
                    tvMovieRuntime.text = "Runtime: $runtimeText"
                    tvMovieRuntime.visibility = View.VISIBLE
                } else {
                    tvMovieRuntime.visibility = View.GONE
                }

                // Display genres
                if (movie.genres.isNotEmpty()) {
                    val genresText = movie.genres.joinToString(", ") { it.name }
                    tvMovieGenres.text = genresText
                    tvMovieGenres.visibility = View.VISIBLE
                } else {
                    tvMovieGenres.visibility = View.GONE
                }

                // Show budget if available
                if (movie.budget > 0) {
                    val budgetText = StringBuilder("Budget: ")
                    budgetText.append(currencyFormat.format(movie.budget))
                    tvMovieBudget.text = budgetText
                    tvMovieBudget.visibility = View.VISIBLE
                } else {
                    tvMovieBudget.visibility = View.GONE
                }

                // Show revenue if available
                if (movie.revenue > 0) {
                    val revenueText = StringBuilder("Revenue: ")
                    revenueText.append(currencyFormat.format(movie.revenue))
                    tvMovieRevenue.text = revenueText
                    tvMovieRevenue.visibility = View.VISIBLE
                } else {
                    tvMovieRevenue.visibility = View.GONE
                }
            }
        }

        // Observe cast information
        viewModel.movieCredits.observe(viewLifecycleOwner) { credits ->
            if (credits.cast.isNotEmpty()) {
                castAdapter.submitList(credits.cast.take(10))
                binding.castSection.visibility = View.VISIBLE
            } else {
                binding.castSection.visibility = View.GONE
            }

            // Show content after loading credits
            binding.contentGroup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.contentGroup.visibility = View.VISIBLE
            }
        }

        // Observe error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}