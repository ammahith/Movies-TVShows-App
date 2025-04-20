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
import com.tictac.moviesapp.databinding.FragmentTvShowDetailBinding
import com.tictac.moviesapp.ui.adapters.CastAdapter
import com.tictac.moviesapp.utils.formatDate
import com.tictac.moviesapp.utils.loadBackdropImage
import com.tictac.moviesapp.utils.loadPosterImage
import java.text.DecimalFormat

class TVShowDetailFragment : Fragment() {
    private var _binding: FragmentTvShowDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TVShowDetailViewModel
    private lateinit var castAdapter: CastAdapter

    // Store scroll position
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvShowDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TVShowDetailViewModel::class.java]
        setupCastRecyclerView()

        // Get TV show ID from arguments
        val tvShowId = arguments?.getInt("tvShowId") ?: -1
        if (tvShowId != -1) {
            viewModel.fetchTVShowDetails(tvShowId)
        } else {
            // Handle error - invalid TV show ID
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, "Invalid TV show ID", Toast.LENGTH_SHORT).show()
        }

        setupObservers()
        setupBackButton()
    }

    override fun onPause() {
        super.onPause()

        // Save scroll position
        binding.nestedScrollView?.let {
            scrollPosition = it.scrollY
        }
    }

    override fun onResume() {
        super.onResume()

        // Restore scroll position
        binding.nestedScrollView?.post {
            binding.nestedScrollView.scrollTo(0, scrollPosition)
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
                Toast.makeText(context, "Could not navigate to person details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }
    }

    private fun setupObservers() {
        // Format decimal to one place
        val ratingFormat = DecimalFormat("#.#")

        // Observe TV show details
        viewModel.tvShow.observe(viewLifecycleOwner) { tvShow ->
            // Handle loading state - hide content initially
            binding.progressBar.visibility = View.VISIBLE
            binding.contentGroup.visibility = View.GONE

            // Set title with null safety
            binding.tvShowTitle.text = tvShow.title ?: "Unknown Title"

            // Set tagline with visibility control
            if (!tvShow.tagline.isNullOrEmpty()) {
                binding.tvShowTagline.text = tvShow.tagline
                binding.tvShowTagline.visibility = View.VISIBLE
            } else {
                binding.tvShowTagline.visibility = View.GONE
            }

            // Set rating with proper formatting
            if (tvShow.rating > 0) {
                binding.tvShowRating.text = getString(R.string.rating_format, ratingFormat.format(tvShow.rating))
                binding.tvShowRating.visibility = View.VISIBLE
            } else {
                binding.tvShowRating.visibility = View.GONE
            }

            // Set release date (firstAirDate for TV shows, releaseDate as fallback)
            if (!tvShow.releaseDate.isNullOrEmpty()) {
                binding.tvShowReleaseDate.text = getString(R.string.first_air_date, tvShow.releaseDate.formatDate())
                binding.tvShowReleaseDate.visibility = View.VISIBLE
            } else {
                binding.tvShowReleaseDate.text = getString(R.string.unknown_air_date)
                binding.tvShowReleaseDate.visibility = View.VISIBLE
            }

            // Set seasons count with null safety and visibility
            if (tvShow.numberOfSeasons != null && tvShow.numberOfSeasons > 0) {
                binding.tvShowSeasons.text = getString(R.string.seasons_count, tvShow.numberOfSeasons.toString())
                binding.tvShowSeasons.visibility = View.VISIBLE
            } else {
                binding.tvShowSeasons.visibility = View.GONE
            }

            // Set episodes count with null safety and visibility
            if (tvShow.numberOfEpisodes != null && tvShow.numberOfEpisodes > 0) {
                binding.tvShowEpisodes.text = getString(R.string.episodes_count, tvShow.numberOfEpisodes.toString())
                binding.tvShowEpisodes.visibility = View.VISIBLE
            } else {
                binding.tvShowEpisodes.visibility = View.GONE
            }

            // Set networks with visibility control
            if (!tvShow.networks.isNullOrEmpty()) {
                val networksText = tvShow.networks.joinToString(", ") { it.name }
                binding.tvShowNetworks.text = getString(R.string.networks_format, networksText)
                binding.tvShowNetworks.visibility = View.VISIBLE
            } else {
                binding.tvShowNetworks.visibility = View.GONE
            }

            // Set status with visibility control
            if (!tvShow.status.isNullOrEmpty()) {
                binding.tvShowStatus.text = getString(R.string.status_format, tvShow.status)
                binding.tvShowStatus.visibility = View.VISIBLE
            } else {
                binding.tvShowStatus.visibility = View.GONE
            }

            // Set genres with visibility control
            if (!tvShow.genres.isNullOrEmpty()) {
                binding.tvShowGenres.text = tvShow.genres.joinToString(", ") { it.name }
                binding.tvShowGenres.visibility = View.VISIBLE
            } else {
                binding.tvShowGenres.visibility = View.GONE
            }

            // Set overview with null or empty check
            binding.tvShowOverview.text = if (!tvShow.overview.isNullOrEmpty()) {
                tvShow.overview
            } else {
                getString(R.string.no_overview)
            }

            // Load images
            binding.ivShowPoster.loadPosterImage(tvShow.posterPath)
            binding.ivShowBackdrop.loadBackdropImage(tvShow.backdropPath)
        }

        // Observe cast information
        viewModel.tvShowCredits.observe(viewLifecycleOwner) { credits ->
            if (credits != null && credits.cast.isNotEmpty()) {
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

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}