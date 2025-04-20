package com.tictac.moviesapp.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tictac.moviesapp.R
import com.tictac.moviesapp.databinding.FragmentDashboardBinding
import com.tictac.moviesapp.ui.MainActivity
import com.tictac.moviesapp.ui.adapters.TVShowAdapter
import com.tictac.moviesapp.utils.Constants

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var tvShowAdapter: TVShowAdapter

    // Store RecyclerView state
    private var recyclerViewState: Parcelable? = null

    // Store scroll position
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipGroup()
        setupSearchBar()
        setupLoadMoreButton()
        observeViewModel()

        // Initial data load
        dashboardViewModel.fetchTVShowsByCategory(Constants.POPULAR)

        // Refresh on swipe
        binding.swipeRefreshLayout.setOnRefreshListener {
            val category = dashboardViewModel.currentCategory.value ?: Constants.POPULAR
            dashboardViewModel.fetchTVShowsByCategory(category)
        }
    }

    override fun onPause() {
        super.onPause()

        // Save scroll position
        scrollPosition = binding.swipeRefreshLayout.scrollY
        (activity as? MainActivity)?.saveScrollPosition(R.id.navigation_dashboard, scrollPosition)

        // Save RecyclerView state
        recyclerViewState = binding.rvMovies.layoutManager?.onSaveInstanceState()
        (activity as? MainActivity)?.saveRecyclerViewState("dashboard_tvshows", recyclerViewState)
    }

    override fun onResume() {
        super.onResume()

        // Restore scroll position
        (activity as? MainActivity)?.getScrollPosition(R.id.navigation_dashboard)?.let {
            scrollPosition = it
            binding.swipeRefreshLayout.post {
                binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
            }
        }

        // Restore RecyclerView state
        (activity as? MainActivity)?.getRecyclerViewState("dashboard_tvshows")?.let {
            recyclerViewState = it
            binding.rvMovies.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun setupSearchBar() {
        val searchInput = binding.searchBar.searchInput
        val clearSearch = binding.searchBar.clearSearch

        searchInput.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    dashboardViewModel.searchTVShows(query)

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
            dashboardViewModel.fetchTVShowsByCategory(Constants.POPULAR)
            // Set the first chip (Popular) as selected
            binding.chipPopular.isChecked = true
        }
    }

    private fun setupLoadMoreButton() {
        binding.btnLoadMore.setOnClickListener {
            dashboardViewModel.loadMoreTVShows()
        }
    }

    private fun setupRecyclerView() {
        tvShowAdapter = TVShowAdapter { tvShow ->
            // Save scroll positions before navigating
            scrollPosition = binding.swipeRefreshLayout.scrollY
            (activity as? MainActivity)?.saveScrollPosition(R.id.navigation_dashboard, scrollPosition)

            // Save RecyclerView state
            recyclerViewState = binding.rvMovies.layoutManager?.onSaveInstanceState()
            (activity as? MainActivity)?.saveRecyclerViewState("dashboard_tvshows", recyclerViewState)

            // Navigate to detail
            val action =
                DashboardFragmentDirections
                    .actionNavigationDashboardToTvShowDetailFragment(tvShow.id)
            findNavController().navigate(action)
        }

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = tvShowAdapter
        }
    }

    private fun setupChipGroup() {
        binding.chipPopular.setOnClickListener {
            binding.tvCategoryTitle.text = getString(R.string.popular_tv_shows)
            dashboardViewModel.fetchTVShowsByCategory(Constants.POPULAR)
        }
        binding.chipAiringToday.setOnClickListener {
            binding.tvCategoryTitle.text = getString(R.string.airing_today_tv_shows)
            dashboardViewModel.fetchTVShowsByCategory(Constants.AIRING_TODAY)
        }
        binding.chipOnTv.setOnClickListener {
            binding.tvCategoryTitle.text = getString(R.string.on_tv_shows)
            dashboardViewModel.fetchTVShowsByCategory(Constants.ON_TV)
        }
        binding.chipTopRated.setOnClickListener {
            binding.tvCategoryTitle.text = getString(R.string.top_rated_tv_shows)
            dashboardViewModel.fetchTVShowsByCategory(Constants.TOP_RATED)
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.tvShows.observe(viewLifecycleOwner) { tvShows ->
            if (tvShows.isEmpty()) {
                tvShowAdapter.submitList(emptyList())
            } else {
                val oldSize = tvShowAdapter.itemCount
                tvShowAdapter.submitList(tvShows)

                // If loading more shows (not initial load), scroll to show the first new item
                if (oldSize > 0 && oldSize < tvShows.size) {
                    binding.rvMovies.postDelayed({
                        // Scroll to the first new item with some offset
                        (binding.rvMovies.layoutManager as GridLayoutManager)
                            .scrollToPositionWithOffset(oldSize - (oldSize % 2), 0)
                    }, 100)
                }
            }
        }

        dashboardViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility =
                if (loading) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = loading

            // After loading completes, restore scroll position
            if (!loading) {
                binding.swipeRefreshLayout.post {
                    binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
                }
            }
        }

        dashboardViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }

        dashboardViewModel.currentCategory.observe(viewLifecycleOwner) { category ->
            binding.tvCategoryTitle.text = when (category) {
                Constants.POPULAR       -> getString(R.string.popular_tv_shows)
                Constants.AIRING_TODAY  -> getString(R.string.airing_today_tv_shows)
                Constants.ON_TV         -> getString(R.string.on_tv_shows)
                Constants.TOP_RATED     -> getString(R.string.top_rated_tv_shows)
                else                   -> "Search Results"
            }
        }

        dashboardViewModel.isSearchMode.observe(viewLifecycleOwner) { isSearch ->
            binding.chipGroup.visibility =
                if (isSearch) View.GONE else View.VISIBLE
            binding.btnLoadMore.visibility =
                if (isSearch) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}