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
import com.tictac.moviesapp.databinding.FragmentPersonDetailBinding
import com.tictac.moviesapp.ui.adapters.MovieAdapter
import com.tictac.moviesapp.utils.loadProfileImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PersonDetailFragment : Fragment() {
    private var _binding: FragmentPersonDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PersonDetailViewModel
    private lateinit var creditsAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PersonDetailViewModel::class.java]

        // Get person ID from arguments
        val personId = arguments?.getInt("personId") ?: -1
        if (personId != -1) {
            viewModel.fetchPersonDetails(personId)
            viewModel.fetchPersonCredits(personId)
        } else {
            // Handle error - invalid person ID
            binding.progressBar.visibility = View.GONE
        }

        setupRecyclerView()
        setupObservers()
        setupBackButton()
    }

    private fun setupRecyclerView() {
        creditsAdapter = MovieAdapter { movie ->
            // Fix navigation to movie detail
            try {
                val bundle = Bundle().apply {
                    putInt("movieId", movie.id)
                }
                // Use the direct ID reference instead of an action
                findNavController().navigate(R.id.movieDetailFragment, bundle)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Could not open details for: ${movie.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.rvPersonCredits.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = creditsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.person.observe(viewLifecycleOwner) { person ->
            binding.tvPersonName.text = person.name
            binding.tvPersonDepartment.text = person.knownForDepartment

            // Calculate age if birthday is available
            if (!person.birthday.isNullOrEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val birthDate = sdf.parse(person.birthday)
                val today = Calendar.getInstance()

                if (birthDate != null) {
                    val birthCalendar = Calendar.getInstance()
                    birthCalendar.time = birthDate

                    var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                    if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }

                    val formattedBirthday = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(birthDate)

                    if (person.deathday.isNullOrEmpty()) {
                        // Use StringBuilder to avoid concatenation
                        val birthdayText = StringBuilder()
                        birthdayText.append("Born: ")
                        birthdayText.append(formattedBirthday)
                        birthdayText.append(" (")
                        birthdayText.append(age)
                        birthdayText.append(" years old)")
                        binding.tvPersonBirthday.text = birthdayText
                    } else {
                        val deathDate = sdf.parse(person.deathday)
                        val formattedDeathday = if (deathDate != null) {
                            SimpleDateFormat("MMMM d, yyyy", Locale.US).format(deathDate)
                        } else {
                            person.deathday
                        }
                        // Use StringBuilder to avoid concatenation
                        val lifeSpanText = StringBuilder()
                        lifeSpanText.append("Born: ")
                        lifeSpanText.append(formattedBirthday)
                        lifeSpanText.append(", Died: ")
                        lifeSpanText.append(formattedDeathday)
                        binding.tvPersonBirthday.text = lifeSpanText
                    }
                }
            }

            binding.tvPersonBiography.text = if (person.biography.isNotEmpty()) {
                person.biography
            } else {
                "No biography available for this person."
            }

            // Load profile image
            binding.ivPersonProfile.loadProfileImage(person.profilePath)

            // Show content
            binding.contentGroup.visibility = View.VISIBLE
        }

        viewModel.personCredits.observe(viewLifecycleOwner) { credits ->
            // Convert cast credits to a compatible format for MovieAdapter
            val movieItems = credits.cast.map { castCredit ->
                com.tictac.moviesapp.data.model.Movie(
                    id = castCredit.id,
                    title = castCredit.displayTitle,
                    overview = "",
                    posterPath = castCredit.posterPath,
                    backdropPath = null,
                    rating = castCredit.voteAverage,
                    releaseDate = castCredit.displayDate,
                    popularity = 0.0,
                    genreIds = emptyList()
                )
            }

            creditsAdapter.submitList(movieItems)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                // Show error message
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