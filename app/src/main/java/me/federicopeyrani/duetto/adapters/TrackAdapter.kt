package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.data.Track
import me.federicopeyrani.duetto.databinding.TrackListItemBinding
import me.federicopeyrani.duetto.fragments.NavigationFragmentDirections

class TrackAdapter(
    private val navController: NavController
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(
        private val binding: TrackListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun navigateToTrackDetail(trackId: String) {
            val direction = NavigationFragmentDirections
                .actionNavigationFragmentToTrackDetailFragment(trackId)
            navController.navigate(direction)
        }

        fun bind(track: Track) {
            binding.track = track
            binding.setClickListener { navigateToTrackDetail(track.id) }
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