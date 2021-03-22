package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.data.Track
import me.federicopeyrani.duetto.databinding.TrackListItemBinding

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(
        private val binding: TrackListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.track = track
        }
    }

    private val trackList = mutableListOf<Track>()

    operator fun plusAssign(list: List<Track>) {
        val previousListSize = trackList.size
        trackList += list
        notifyItemRangeInserted(previousListSize, list.size)
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
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int = trackList.size
}