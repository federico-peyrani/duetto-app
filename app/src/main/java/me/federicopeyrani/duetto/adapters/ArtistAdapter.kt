package me.federicopeyrani.duetto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.federicopeyrani.duetto.data.Artist
import me.federicopeyrani.duetto.databinding.GridItemArtistBinding

class ArtistAdapter : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    inner class ArtistViewHolder(
        private val binding: GridItemArtistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.artist = artist
        }
    }

    private val artistList = mutableListOf<Artist>()

    operator fun plusAssign(list: List<Artist>) {
        val previousListSize = artistList.size
        artistList += list
        notifyItemRangeInserted(previousListSize, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ArtistViewHolder {
        val binding = GridItemArtistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artistList[position])
    }

    override fun getItemCount(): Int = artistList.size
}