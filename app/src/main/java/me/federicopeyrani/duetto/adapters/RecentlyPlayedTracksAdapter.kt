package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.adapters.RecentlyPlayedTracksAdapter.TrackViewHolder
import me.federicopeyrani.duetto.data.Track
import me.federicopeyrani.duetto.databinding.TrackListItemBinding

class RecentlyPlayedTracksAdapter : PagingDataAdapter<Track, TrackViewHolder>(ItemCallback) {

    class TrackViewHolder(
        private val binding: TrackListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.track = track
        }
    }

    companion object ItemCallback : DiffUtil.ItemCallback<Track>() {

        override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackViewHolder {
        val binding = TrackListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}