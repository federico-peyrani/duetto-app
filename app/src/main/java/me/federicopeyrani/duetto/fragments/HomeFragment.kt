package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.adapters.ArtistAdapter
import me.federicopeyrani.duetto.adapters.TrackAdapter
import me.federicopeyrani.duetto.databinding.FragmentHomeBinding
import me.federicopeyrani.duetto.viewmodels.HomeViewModel
import me.federicopeyrani.duetto.views.StackedBarsGraphView

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private suspend fun setTopGenres() {
        val genres = withContext(Dispatchers.IO) { viewModel.getTopGenres() }
        binding.topGenresCard.graph.bars = genres.map {
            StackedBarsGraphView.Bar(it.key, it.value.toFloat())
        }
    }

    private suspend fun setTopTracks(adapter: TrackAdapter) {
        val tracks = withContext(Dispatchers.IO) { viewModel.getTopTracks() }
        adapter += tracks
    }

    private suspend fun setTopArtists(adapter: ArtistAdapter) {
        val artists = withContext(Dispatchers.IO) { viewModel.getTopArtists() }
        adapter += artists
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // get top-level nav controller
        val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host)

        val trackAdapter = TrackAdapter(mainNavController)
        binding.topTracksCard.rank.adapter = trackAdapter

        val artistAdapter = ArtistAdapter()
        val artistLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.topArtistsCard.rank.adapter = artistAdapter
        binding.topArtistsCard.rank.layoutManager = artistLayoutManager

        lifecycleScope.launchWhenCreated {
            launch { setTopGenres() }
            launch { setTopTracks(trackAdapter) }
            launch { setTopArtists(artistAdapter) }
        }
    }
}