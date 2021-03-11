package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.databinding.FragmentHomeBinding
import me.federicopeyrani.duetto.viewmodels.HomeViewModel

@AndroidEntryPoint
class FragmentHome : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        lifecycleScope.launchWhenStarted {
            val track = viewModel.getCurrentPlaybackFlow()
            launch { track.title.collect { binding.currentPlaying.songTitle.text = it } }
            launch { track.artist.collect { binding.currentPlaying.songArtist.text = it } }
        }
    }
}