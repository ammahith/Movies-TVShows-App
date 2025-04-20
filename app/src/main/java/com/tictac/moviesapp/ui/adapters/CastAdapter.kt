package com.tictac.moviesapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tictac.moviesapp.data.model.CastMember
import com.tictac.moviesapp.databinding.ItemCastBinding
import com.tictac.moviesapp.utils.loadProfileImage

class CastAdapter(private val onItemClick: (CastMember) -> Unit) :
    ListAdapter<CastMember, CastAdapter.CastViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(castMember: CastMember) {
            binding.tvActorName.text = castMember.name
            binding.tvCharacterName.text = castMember.character
            binding.ivActorPhoto.loadProfileImage(castMember.profilePath)

            binding.root.setOnClickListener {
                onItemClick(castMember)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}