package com.tictac.moviesapp.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tictac.moviesapp.R
import com.tictac.moviesapp.data.model.Person
import com.tictac.moviesapp.databinding.ItemPersonBinding
import com.tictac.moviesapp.utils.loadProfileImage

class PersonAdapter(private val onItemClick: (Person) -> Unit) :
    ListAdapter<Person, PersonAdapter.PersonViewHolder>(DIFF_CALLBACK) {

    // Keep track of the current list for pagination
    private val currentPersonList = mutableListOf<Person>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Person>?) {
        if (list != null && list != currentPersonList) {
            currentPersonList.clear()
            currentPersonList.addAll(list)
        }
        super.submitList(list?.toList()) // Create a new list to force DiffUtil to run
    }

    inner class PersonViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.tvPersonName.text = person.name ?: "Unknown"

            // Format "Known For" text safely
            val knownForTitles = person.knownFor.mapNotNull {
                it.title ?: it.name
            }.take(2)

            binding.tvPersonKnownFor.text = if (knownForTitles.isNotEmpty()) {
                knownForTitles.joinToString(", ")
            } else {
                "Unknown credits"
            }

            // Load image with null safety
            binding.ivPersonPhoto.loadProfileImage(person.profilePath)

            // Set up click handling that navigates directly using the navigation component
            binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("personId", person.id)
                }

                try {
                    // Use findNavController directly to navigate to person detail
                    binding.root.findNavController().navigate(
                        R.id.action_navigation_notifications_to_personDetailFragment,
                        bundle
                    )
                } catch (e: Exception) {
                    // Fall back to the provided click handler if navigation fails
                    onItemClick(person)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Person>() {
            override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
                return oldItem == newItem
            }
        }
    }
}