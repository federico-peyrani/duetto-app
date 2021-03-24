package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.federicopeyrani.duetto.databinding.FragmentTrackDetailBinding

@AndroidEntryPoint
class FragmentTrackDetail : Fragment() {

    private lateinit var binding: FragmentTrackDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrackDetailBinding.bind(view)
    }
}