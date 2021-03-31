package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.data.Artist
import me.federicopeyrani.duetto.databinding.ChipArtistBinding
import me.federicopeyrani.duetto.databinding.FragmentTrackDetailBinding
import me.federicopeyrani.duetto.viewmodels.TrackDetailViewModel
import javax.inject.Inject

@AndroidEntryPoint
class TrackDetailFragment : Fragment(R.layout.fragment_track_detail) {

    private val args: TrackDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTrackDetailBinding

    @Inject lateinit var viewModelFactory: TrackDetailViewModel.Factory

    private val viewModel: TrackDetailViewModel by viewModels {
        TrackDetailViewModel.create(viewModelFactory, args.trackId)
    }

    private fun createChip(artist: Artist, swatch: Palette.Swatch) {
        val chip = ChipArtistBinding.inflate(layoutInflater, binding.trackArtists, false)
        chip.artist = artist
        chip.swatch = swatch
        binding.trackArtists.addView(chip.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrackDetailBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        lifecycleScope.launchWhenCreated {
            val trackFlow = viewModel.track.filterNotNull()
            val artistChipSwatchFlow = viewModel.artistChipSwatch.filterNotNull()
            combine(trackFlow, artistChipSwatchFlow) { track, swatch -> track to swatch }
                .collectLatest { (track, swatch) ->
                    binding.trackArtists.removeAllViews()
                    track.artist.forEach { createChip(it, swatch) }
                }
        }
    }
}