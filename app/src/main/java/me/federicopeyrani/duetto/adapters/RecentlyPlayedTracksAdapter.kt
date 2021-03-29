package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.adapters.RecentlyPlayedTracksAdapter.TrackViewHolder
import me.federicopeyrani.duetto.data.PlayHistory
import me.federicopeyrani.duetto.databinding.ListItemPlayHistoryBinding

class RecentlyPlayedTracksAdapter : PagingDataAdapter<PlayHistory, TrackViewHolder>(ItemCallback) {

    private object ItemCallback : DiffUtil.ItemCallback<PlayHistory>() {

        override fun areItemsTheSame(oldItem: PlayHistory, newItem: PlayHistory) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: PlayHistory, newItem: PlayHistory) =
            oldItem == newItem
    }

    class TrackViewHolder(
        private val binding: ListItemPlayHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playHistory: PlayHistory) {
            binding.playHistory = playHistory
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackViewHolder {
        val binding = ListItemPlayHistoryBinding.inflate(
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