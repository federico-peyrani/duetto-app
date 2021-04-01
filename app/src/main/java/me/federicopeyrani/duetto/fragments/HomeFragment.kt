package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.adapters.ArtistAdapter
import me.federicopeyrani.duetto.adapters.TrackAdapter
import me.federicopeyrani.duetto.databinding.FragmentHomeBinding
import me.federicopeyrani.duetto.viewmodels.HomeViewModel
import me.federicopeyrani.duetto.views.StackedBarsGraphView
import java.io.IOException

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    /**
     * Handles exceptions raised while retrieving the data used to populate the cards on the screen.
     */
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val message = when (throwable) {
            is IOException -> getString(R.string.error_network, throwable.localizedMessage)
            else -> getString(R.string.error_generic)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE).show()
    }

    private suspend fun setTopGenres() {
        val genres = viewModel.getTopGenres()
        binding.topGenresCard.graph.bars = genres.map {
            StackedBarsGraphView.Bar(it.key, it.value.toFloat())
        }
    }

    private suspend fun setTopTracks(adapter: TrackAdapter) {
        val tracks = viewModel.getTopTracks()
        adapter += tracks
    }

    private suspend fun setTopArtists(adapter: ArtistAdapter) {
        val artists = viewModel.getTopArtists()
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
            // Create a new children job with three children jobs of its own to be executed in
            // parallel: passing Job() to the constructor ensures that if the new job fails,
            // the parent job won't fail as well and exceptions won't be propagated, but at the same
            // time ensures that if any of the three children job fails also the others will fail.
            launch(Job() + coroutineExceptionHandler) {
                launch { setTopGenres() }
                launch { setTopTracks(trackAdapter) }
                launch { setTopArtists(artistAdapter) }
            }
        }
    }
}