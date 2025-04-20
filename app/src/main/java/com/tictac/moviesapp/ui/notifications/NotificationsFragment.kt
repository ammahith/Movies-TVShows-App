package com.tictac.moviesapp.ui.notifications

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
import androidx.recyclerview.widget.GridLayoutManager
import com.tictac.moviesapp.R
import com.tictac.moviesapp.databinding.FragmentNotificationsBinding
import com.tictac.moviesapp.ui.MainActivity
import com.tictac.moviesapp.ui.adapters.PersonAdapter

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var personAdapter: PersonAdapter

    // Store RecyclerView state
    private var recyclerViewState: Parcelable? = null

    // Store scroll position
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        setupLoadMoreButton()
        observeViewModel()

        // Initial data load
        notificationsViewModel.fetchPopularPeople()

        // Refresh on swipe
        binding.swipeRefreshLayout.setOnRefreshListener {
            notificationsViewModel.fetchPopularPeople()
        }
    }

    override fun onPause() {
        super.onPause()

        // Save scroll position
        scrollPosition = binding.swipeRefreshLayout.scrollY
        (activity as? MainActivity)?.saveScrollPosition(R.id.navigation_notifications, scrollPosition)

        // Save RecyclerView state
        recyclerViewState = binding.rvPeople.layoutManager?.onSaveInstanceState()
        (activity as? MainActivity)?.saveRecyclerViewState("notifications_people", recyclerViewState)
    }

    override fun onResume() {
        super.onResume()

        // Restore scroll position
        (activity as? MainActivity)?.getScrollPosition(R.id.navigation_notifications)?.let {
            scrollPosition = it
            binding.swipeRefreshLayout.post {
                binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
            }
        }

        // Restore RecyclerView state
        (activity as? MainActivity)?.getRecyclerViewState("notifications_people")?.let {
            recyclerViewState = it
            binding.rvPeople.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun setupSearchBar() {
        val searchInput = binding.searchBar.searchInput
        val clearSearch = binding.searchBar.clearSearch

        searchInput.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    notificationsViewModel.searchPeople(query)

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
            notificationsViewModel.fetchPopularPeople()
        }
    }

    private fun setupRecyclerView() {
        personAdapter = PersonAdapter { /* You can leave this empty if navigation is handled in adapter */ }

        binding.rvPeople.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = personAdapter
        }
    }

    private fun setupLoadMoreButton() {
        binding.btnLoadMore.setOnClickListener {
            notificationsViewModel.loadMorePeople()
        }
    }

    private fun observeViewModel() {
        notificationsViewModel.people.observe(viewLifecycleOwner) { people ->
            if (people.isEmpty()) {
                personAdapter.submitList(emptyList())
            } else {
                val oldSize = personAdapter.itemCount
                personAdapter.submitList(people)

                // If loading more people (not initial load), scroll to show new items
                if (oldSize > 0 && oldSize < people.size) {
                    binding.rvPeople.postDelayed({
                        // Scroll to the first new item with some offset
                        (binding.rvPeople.layoutManager as GridLayoutManager)
                            .scrollToPositionWithOffset(oldSize - (oldSize % 2), 0)
                    }, 100)
                }
            }
        }

        notificationsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = isLoading

            // After loading completes, restore scroll position
            if (!isLoading) {
                binding.swipeRefreshLayout.post {
                    binding.swipeRefreshLayout.scrollTo(0, scrollPosition)
                }
            }
        }

        notificationsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        notificationsViewModel.isSearchMode.observe(viewLifecycleOwner) { isSearch ->
            // Update the title text
            binding.tvPeopleTitle.text = if (isSearch) "Search Results" else "Popular People"

            // Update Load More button visibility
            binding.btnLoadMore.visibility = if (isSearch) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}