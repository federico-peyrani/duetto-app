package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.adapters.RecentlyPlayedTracksAdapter.TrackViewHolder
import me.federicopeyrani.duetto.data.PlayHistory
import me.federicopeyrani.duetto.databinding.PlayHistoryListItemBinding

class RecentlyPlayedTracksAdapter : PagingDataAdapter<PlayHistory, TrackViewHolder>(ItemCallback) {

    class TrackViewHolder(
        private val binding: PlayHistoryListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playHistory: PlayHistory) {
            binding.playHistory = playHistory
        }
    }

    companion object ItemCallback : DiffUtil.ItemCallback<PlayHistory>() {

        override fun areItemsTheSame(oldItem: PlayHistory, newItem: PlayHistory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PlayHistory, newItem: PlayHistory) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackViewHolder {
        val binding = PlayHistoryListItemBinding.inflate(
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