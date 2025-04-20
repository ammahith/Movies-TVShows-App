package com.tictac.moviesapp.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tictac.moviesapp.R
import com.tictac.moviesapp.data.model.Movie
import com.tictac.moviesapp.databinding.FragmentHomeBinding
import com.tictac.moviesapp.ui.MainActivity
import com.tictac.moviesapp.ui.adapters.MovieAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var popularMoviesAdapter: MovieAdapter
    private lateinit var nowPlayingMoviesAdapter: MovieAdapter
    private lateinit var topRatedMoviesAdapter: MovieAdapter
    private lateinit var upcomingMoviesAdapter: MovieAdapter
    private lateinit var trendingMoviesAdapter: MovieAdapter

    // Store RecyclerView states
    private var popularMoviesState: Parcelable? = null
    private var nowPlayingMoviesState: Parcelable? = null
    private var topRatedMoviesState: Parcelable? = null
    private var upcomingMoviesState: Parcelable? = null
    private var trendingMoviesState: Parcelable? = null

    // Store scroll position
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupLoadMoreButtons()
        setupSearchBar()
        observeViewModel()

        // Initial data load
        homeViewModel.fetchAllMovies()

        // Refresh on swipe
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.fetchAllMovies()
        }
    }

    override fun onPause() {
        super.onPause()

        // Save scroll position
        scrollPosition = binding.swipeRefreshLayout.scrollY
        (activity as? MainActivity)?.saveScrollPosition(R.id.navigation_home, scrollPosition)

        // Save RecyclerView states
        popularMoviesState = binding.rvPopularMovies.layoutManager?.onSaveInstanceState()
        nowPlayingMoviesState = binding.rvNowPlayingMovies.layoutManager?.onSaveInstanceState()
        topRatedMoviesState = binding.rvTopRatedMovies.layoutManager?.onSaveInstanceState()
        upcomingMoviesState = binding.rvUpcomingMovies.layoutManager?.onSaveInstanceState()
        trendingMoviesState = binding.rvTrendingMovies.layoutManager?.onSaveInstanceState()

        // Save to MainActivity
        (activity as? MainActivity)?.also { mainActivity ->
            mainActivity.saveRecyclerViewState("home_popular", popularMoviesState)
            mainActivity.saveRecyclerViewState("home_now_playing", nowPlayingMoviesState)
            mainActivity.saveRecyclerViewState("home_top_rated", topRatedMoviesState)
            mainActivity.saveRecyclerViewState("home_upcoming", upcomingMoviesState)
            mainActivity.saveRecyclerViewState("home_trending", trendingMoviesState)
        }
    }

    override fun onResume() {
        super.onResume()

        // Restore scroll position
        (activity as? MainActivity)?.getScrollPosition(R.id.navigation_home)?.let {
            scrollPosition = it
            binding.swipeRefreshLayout.post {
                binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
            }
        }

        // Restore RecyclerView states
        (activity as? MainActivity)?.also { mainActivity ->
            popularMoviesState = mainActivity.getRecyclerViewState("home_popular")
            nowPlayingMoviesState = mainActivity.getRecyclerViewState("home_now_playing")
            topRatedMoviesState = mainActivity.getRecyclerViewState("home_top_rated")
            upcomingMoviesState = mainActivity.getRecyclerViewState("home_upcoming")
            trendingMoviesState = mainActivity.getRecyclerViewState("home_trending")
        }

        // Apply states if available
        restoreRecyclerViewStates()
    }

    private fun restoreRecyclerViewStates() {
        // Apply saved states to RecyclerViews
        popularMoviesState?.let {
            binding.rvPopularMovies.layoutManager?.onRestoreInstanceState(it)
        }

        nowPlayingMoviesState?.let {
            binding.rvNowPlayingMovies.layoutManager?.onRestoreInstanceState(it)
        }

        topRatedMoviesState?.let {
            binding.rvTopRatedMovies.layoutManager?.onRestoreInstanceState(it)
        }

        upcomingMoviesState?.let {
            binding.rvUpcomingMovies.layoutManager?.onRestoreInstanceState(it)
        }

        trendingMoviesState?.let {
            binding.rvTrendingMovies.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun setupSearchBar() {
        val searchInput = binding.searchBar.searchInput
        val clearSearch = binding.searchBar.clearSearch

        searchInput.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    homeViewModel.searchMovies(query)

                    // Hide keyboard
                    (requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(textView.windowToken, 0)

                    clearSearch.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }

        clearSearch.setOnClickListener {
            searchInput.text.clear()
            clearSearch.visibility = View.GONE
            homeViewModel.fetchAllMovies()
        }
    }

    private fun setupRecyclerViews() {
        // Popular
        popularMoviesAdapter = MovieAdapter { movie ->
            navigateToMovieDetailScreen(movie)
        }
        binding.rvPopularMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularMoviesAdapter
        }

        // Now playing
        nowPlayingMoviesAdapter = MovieAdapter { movie ->
            navigateToMovieDetailScreen(movie)
        }
        binding.rvNowPlayingMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = nowPlayingMoviesAdapter
        }

        // Top rated
        topRatedMoviesAdapter = MovieAdapter { movie ->
            navigateToMovieDetailScreen(movie)
        }
        binding.rvTopRatedMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = topRatedMoviesAdapter
        }

        // Upcoming
        upcomingMoviesAdapter = MovieAdapter { movie ->
            navigateToMovieDetailScreen(movie)
        }
        binding.rvUpcomingMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingMoviesAdapter
        }

        // Trending
        trendingMoviesAdapter = MovieAdapter { movie ->
            navigateToMovieDetailScreen(movie)
        }
        binding.rvTrendingMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingMoviesAdapter
        }
    }

    private fun setupLoadMoreButtons() {
        binding.btnLoadMorePopular.setOnClickListener {
            homeViewModel.loadMorePopularMovies()
        }

        binding.btnLoadMoreNowPlaying.setOnClickListener {
            homeViewModel.loadMoreNowPlayingMovies()
        }

        binding.btnLoadMoreTopRated.setOnClickListener {
            homeViewModel.loadMoreTopRatedMovies()
        }

        binding.btnLoadMoreUpcoming.setOnClickListener {
            homeViewModel.loadMoreUpcomingMovies()
        }

        binding.btnLoadMoreTrending.setOnClickListener {
            homeViewModel.loadMoreTrendingMovies()
        }
    }

    private fun observeViewModel() {
        homeViewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                popularMoviesAdapter.submitList(emptyList())
            } else {
                val oldSize = popularMoviesAdapter.itemCount
                popularMoviesAdapter.submitList(movies)
                if (oldSize > 0 && oldSize < movies.size) {
                    // If we've added new items (not initial load), scroll to show the new items
                    binding.rvPopularMovies.smoothScrollToPosition(oldSize)
                }
            }
        }

        homeViewModel.nowPlayingMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                nowPlayingMoviesAdapter.submitList(emptyList())
            } else {
                val oldSize = nowPlayingMoviesAdapter.itemCount
                nowPlayingMoviesAdapter.submitList(movies)
                if (oldSize > 0 && oldSize < movies.size) {
                    binding.rvNowPlayingMovies.smoothScrollToPosition(oldSize)
                }
            }
        }

        homeViewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                topRatedMoviesAdapter.submitList(emptyList())
            } else {
                val oldSize = topRatedMoviesAdapter.itemCount
                topRatedMoviesAdapter.submitList(movies)
                if (oldSize > 0 && oldSize < movies.size) {
                    binding.rvTopRatedMovies.smoothScrollToPosition(oldSize)
                }
            }
        }

        homeViewModel.upcomingMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                upcomingMoviesAdapter.submitList(emptyList())
            } else {
                val oldSize = upcomingMoviesAdapter.itemCount
                upcomingMoviesAdapter.submitList(movies)
                if (oldSize > 0 && oldSize < movies.size) {
                    binding.rvUpcomingMovies.smoothScrollToPosition(oldSize)
                }
            }
        }

        homeViewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                trendingMoviesAdapter.submitList(emptyList())
            } else {
                val oldSize = trendingMoviesAdapter.itemCount
                trendingMoviesAdapter.submitList(movies)
                if (oldSize > 0 && oldSize < movies.size) {
                    binding.rvTrendingMovies.smoothScrollToPosition(oldSize)
                }
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading

            // After loading completes, restore scroll position
            if (!isLoading) {
                binding.swipeRefreshLayout.post {
                    binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
                }
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }

        homeViewModel.isSearchMode.observe(viewLifecycleOwner) { isSearch ->
            // Update the visibility of load more buttons
            val loadMoreButtonsVisibility = if (isSearch) View.GONE else View.VISIBLE

            // Update visibility of load more buttons and sections
            binding.btnLoadMorePopular.visibility = loadMoreButtonsVisibility
            binding.btnLoadMoreNowPlaying.visibility = loadMoreButtonsVisibility
            binding.btnLoadMoreTopRated.visibility = loadMoreButtonsVisibility
            binding.btnLoadMoreUpcoming.visibility = loadMoreButtonsVisibility
            binding.btnLoadMoreTrending.visibility = loadMoreButtonsVisibility

            binding.nowPlayingTitle.visibility = if (isSearch) View.GONE else View.VISIBLE
            binding.rvNowPlayingMovies.visibility = if (isSearch) View.GONE else View.VISIBLE

            binding.topRatedTitle.visibility = if (isSearch) View.GONE else View.VISIBLE
            binding.rvTopRatedMovies.visibility = if (isSearch) View.GONE else View.VISIBLE

            binding.upcomingTitle.visibility = if (isSearch) View.GONE else View.VISIBLE
            binding.rvUpcomingMovies.visibility = if (isSearch) View.GONE else View.VISIBLE

            binding.trendingTitle.visibility = if (isSearch) View.GONE else View.VISIBLE
            binding.rvTrendingMovies.visibility = if (isSearch) View.GONE else View.VISIBLE

            // Update the title text
            binding.popularTitle.text = if (isSearch) getString(R.string.search_results) else getString(R.string.popular)
        }
    }

    private fun navigateToMovieDetailScreen(movie: Movie) {
        // Save scroll positions before navigating
        scrollPosition = binding.swipeRefreshLayout.scrollY
        (activity as? MainActivity)?.saveScrollPosition(R.id.navigation_home, scrollPosition)

        // Save RecyclerView states
        (activity as? MainActivity)?.also { mainActivity ->
            mainActivity.saveRecyclerViewState("home_popular",
                binding.rvPopularMovies.layoutManager?.onSaveInstanceState())
            mainActivity.saveRecyclerViewState("home_now_playing",
                binding.rvNowPlayingMovies.layoutManager?.onSaveInstanceState())
            mainActivity.saveRecyclerViewState("home_top_rated",
                binding.rvTopRatedMovies.layoutManager?.onSaveInstanceState())
            mainActivity.saveRecyclerViewState("home_upcoming",
                binding.rvUpcomingMovies.layoutManager?.onSaveInstanceState())
            mainActivity.saveRecyclerViewState("home_trending",
                binding.rvTrendingMovies.layoutManager?.onSaveInstanceState())
        }

        // Navigate to detail
        val action = HomeFragmentDirections.actionNavigationHomeToMovieDetailFragment(movie.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}