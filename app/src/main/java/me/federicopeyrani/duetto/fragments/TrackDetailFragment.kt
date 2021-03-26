package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.databinding.FragmentTrackDetailBinding
import me.federicopeyrani.duetto.viewmodels.TrackDetailViewModel
import javax.inject.Inject

@AndroidEntryPoint
class TrackDetailFragment : Fragment(R.layout.fragment_track_detail) {

    private val args: TrackDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTrackDetailBinding

    @Inject
    lateinit var viewModelFactory: TrackDetailViewModel.Factory

    private val viewModel: TrackDetailViewModel by viewModels {
        TrackDetailViewModel.create(viewModelFactory, args.trackId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrackDetailBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
}